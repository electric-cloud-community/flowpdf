## === rest client imports starts ===
package FlowPlugin::SampleGithubRESTRESTClient;
use strict;
use warnings;
use LWP::UserAgent;
use JSON;
use Data::Dumper;
use FlowPDF::Client::REST;
use FlowPDF::Log;
## === rest client imports ends, checksum: 877ea7f66adab8b6bd67aa60e4456e18 ===
# Place for the custom user imports, e.g. use File::Spec
use Carp qw(croak);
## === rest client starts ===
=head1

FlowPlugin::SampleGithubRESTRESTClient->new('http://endpoint', {
    auth => {basicAuth => {userName => 'admin', password => 'changeme'}}
})

=cut

# Generated
use constant {
    BEARER_PREFIX => 'Bearer',
    USER_AGENT => 'My Github Client',
    OAUTH1_SIGNATURE_METHOD =>  'RSA-SHA1' ,
    CONTENT_TYPE => 'application/json'
};

=pod

Use this method to create a new FlowPlugin::SampleGithubRESTRESTClient, e.g.

    my $client = FlowPlugin::SampleGithubRESTRESTClient->new($endpoint,
        basicAuth => {userName => 'user', password => 'password'}
    );

    my $client = FlowPlugin::SampleGithubRESTRESTClient->new($endpoint,
        bearerAuth => {token => 'token'}
    )

    my $client = FlowPlugin::SampleGithubRESTRESTClient->new($endpoint,
        bearerAuth => {token => 'token'},
        proxy => {url => 'proxy url', username => 'proxy user', password => 'password'}
    )

=cut

sub new {
    my ($class, $endpoint, %params) = @_;

    my $self = { %params };
    $self->{endpoint} = $endpoint;
    if ($self->{basicAuth}) {
        logDebug("Basic Auth Scheme");
        unless($self->{basicAuth}->{userName}) {
            die "No username was provided for the Basic auth";
        }
        unless($self->{basicAuth}->{password}) {
            die "No password was provided for the Basic auth";
        }
    }
    elsif ($self->{bearerAuth}) {
        logDebug("Bearer Auth Scheme");
        unless($self->{bearerAuth}->{token}) {
            die 'No token was provided for the Bearer auth';
        }
    }
    elsif ($self->{oauth1}) {
        logDebug("OAuth 1.0 Auth Scheme");
        unless($self->{oauth1}->{token}) {
            die 'No token is provided for OAuth1 scheme';
        }
        unless($self->{oauth1}->{consumerKey}) {
            die 'No consumerKey is provided for Oauth1 scheme';
        }
        unless ($self->{oauth1}->{privateKey}) {
            die 'No privateKey is provided for oauth1 scheme';
        }
    }
    if ($self->{proxy}) {
        my $url = $self->{proxy}->{url};
        unless($url) {
            die "No URL was provided for the proxy configuration";
        }
        if ($self->{proxy}->{username} && !$self->{proxy}->{password}) {
            die "Username and password should be provided for the proxy auth";
        }
    }

    return bless $self, $class;
}

# This is the simplified form to create the REST client object directly from the plugin configuration
sub createFromConfig {
    my ($class, $configParams) = @_;

    my $endpoint = $configParams->{endpoint};

    my %construction_params = ();
    if  ($configParams->{httpProxyUrl}) {
        $construction_params{proxy} = {
            url => $configParams->{httpProxyUrl},
            username => $configParams->{proxy_user},
            password => $configParams->{proxy_password},
        }
    }
    unless($endpoint) {
        die "No endpoint parameter is provided";
    }
    if ($configParams->{basic_user} && $configParams->{basic_password}) {
        $construction_params{basicAuth} = {
            userName => $configParams->{basic_user},
            password => $configParams->{basic_password}
        };
        return $class->new($endpoint, %construction_params);
    }
    elsif ($configParams->{bearer_password}) {
        $construction_params{bearerAuth} = {
            token => $configParams->{bearer_password},
        };
        return $class->new($endpoint, %construction_params);
    }
    elsif ($configParams->{authScheme} eq 'anonymous') {
        return $class->new($endpoint, %construction_params);
    }
    elsif ($configParams->{oauth1_user}) {
        $construction_params{oauth1} = {
            privateKey => $configParams->{oauth1_password},
            token => $configParams->{oauth1_user},
            consumerKey => $configParams->{oauth1ConsumerKey},
        };
        return $class->new($endpoint, %construction_params);
    }
    return $class->new($endpoint, %$configParams, %construction_params);
}

sub makeRequest {
    my ($self, $method, $uri, $query, $payload, $headers, $params) = @_;

    my $request = $self->createRequest($method, $uri, $query, $payload, $headers);
    logDebug("Request before augment", $request);
    # generic augment
    $request = $self->augmentRequest($request, $params);
    logDebug("Request after augment", $request);
    my $response = $self->rest->doRequest($request);
    logDebug("Response", $response);
    my $retval = $self->processResponse($response);
    if ($retval) {
        return $retval;
    }
    if ($response->is_success) {
        my $parsed = $self->parseResponse($response);
        return $parsed;
    }
    else {
        die "Request for $uri failed: " . $response->status_line;
    }
}

sub method {
    return shift->{method};
}

sub rest {
    my ($self, %params) = @_;

    my $requestMethod = $params{requestMethod} || 'POST';
    unless ($self->{rest}->{$requestMethod}) {
        my $p = {
        };
        $p->{ua} = LWP::UserAgent->new(agent => USER_AGENT);

          # agent                   "libwww-perl/#.###"
          # from                    undef
          # conn_cache              undef
          # cookie_jar              undef
          # default_headers         HTTP::Headers->new
          # local_address           undef
          # ssl_opts                { verify_hostname => 1 }
          # max_size                undef
          # max_redirect            7
          # parse_head              1
          # protocols_allowed       undef
          # protocols_forbidden     undef
          # requests_redirectable   ['GET', 'HEAD']
          # timeout                 180

        if ($self->{proxy}) {
            $p->{proxy} = $self->{proxy};
        }
        if ($self->{oauth1}) {
            my $oauth = $self->{oauth1};
            $p->{oauth} = {
                oauth_version => '1.0',
                oauth_signature_method => OAUTH1_SIGNATURE_METHOD,
                request_method => $requestMethod,
                request_token_path => 'plugins/servlet/oauth/request-token',
                base_url => $self->{endpoint},
                # todo validate
                private_key => $oauth->{privateKey},
                oauth_token => $oauth->{token},
                oauth_consumer_key => $oauth->{consumerKey},
            };
        }
        $self->{rest} = FlowPDF::Client::REST->new($p);
    }
    return $self->{rest};
}

sub createRequest {
    my ($self, $method, $uri, $query, $payload, $headers) = @_;

    $uri =~ s{^/+}{};
    my $endpoint = $self->{endpoint};
    $endpoint =~ s{/+$}{};

    my $rest = $self->rest(requestMethod => $method);
    my $url = URI->new($endpoint . "/$uri");

    if ($query) {
        $url->query_form($url->query_form, %$query);
    }

    if ($self->{oauth1}) {
        require FlowPDF::ComponentManager;
        my $oauth = FlowPDF::ComponentManager->getComponent('FlowPDF::Component::OAuth');
        my $requestParams = $oauth->augment_params_with_oauth($method, $url, $query);
        $url = $rest->augmentUrlWithParams($url, $requestParams);
    }
    my $request = $rest->newRequest($method => $url);

    # auth
    if ($self->{basicAuth}) {
        my $auth = $self->{basicAuth};
        my $username = $auth->{userName};
        my $password = $auth->{password};
        $request->authorization_basic($username, $password);
        logDebug("Added basic auth");
    }
    if ($self->{bearerAuth}) {
        my $auth = $self->{bearerAuth};
        my $token = $auth->{token};
        my $prefix = BEARER_PREFIX;
        $request->header("Authorization", "$prefix $token");
        logDebug("Added bearer auth");
    }

    if ($method eq 'POST' || $method eq 'PUT') {
        $request->header('Content-Type' => CONTENT_TYPE);
    }

    if ($headers) {
        for my $headerName (keys %$headers) {
            $request->header($headerName => $headers->{$headerName});
        }
    }

    if ($payload) {
        logDebug("Payload:", $payload);
        my $encoded = $self->encodePayload($payload);
        $request->content($encoded);
    }

    # proxy should be handled somewhere in the rest
    logDebug("Request: ", $request);

    my $augmentMethod = $self->method . 'AugmentRequest';
    if ($self->can($augmentMethod)) {
        $request = $self->$augmentMethod($request);
    }

    return $request;
}

sub cleanEmptyFields {
    my ($self, $payload) = @_;

    for my $key (keys %$payload) {
        unless ($payload->{$key}) {
            delete $payload->{$key};
        }
        if (ref $payload->{$key} eq 'HASH') {
            $payload->{$key} = $self->cleanEmptyFields($payload->{$key});
        }
    }
    return $payload;
}

sub populateFields {
    my ($self, $object, $params) = @_;

    my $render = sub {
        my ($string) = @_;

        no warnings;
        $string =~ s/(\{\{(\w+)\}\})/$params->{$2}/;
        return $string;
    };

    for my $key (keys %$object) {
        my $value = $object->{$key};
        if (ref $value) {
            if (ref $value eq 'HASH') {
                $object->{$key} = $self->populateFields($value, $params);
            }
            elsif (ref $value eq 'ARRAY') {
                for my $row (@$value) {
                    $row = $self->populateFields($row, $params);
                    # todo fix ref
                }
            }
        }
        else {
            $object->{$key} = $render->($value);
        }
    }
    return $object;
}

sub renderOneLineTemplate {
    my ($template, %params) = @_;

    for my $key (keys %params) {
        $template =~ s/\{\{$key\}\}/$params{$key}/g;
    }
    return $template;
}

# Generated code for the endpoint

# Do not change this code

# name: in query

# repositoryOwner: in path

# releaseId: in path

# repositoryName: in path

sub uploadReleaseAsset {
    my ($self, %params) = @_;

    my $override = $self->can("uploadReleaseAssetOverride");
    if ($override) {
        return $self->$override(%params);
    }

    my $uri = "/repos/{{repositoryOwner}}/{{repositoryName}}/releases/{{releaseId}}/assets";
    $uri = renderOneLineTemplate($uri, %params);
    my $query = {

        'name' => $params{ name },

    };

    # TODO handle credentials
    my $payload = {

    };
    logDebug($payload);

    $self->{method} = 'uploadReleaseAsset';

    my $headers = {
    };

    # Creating a request object
    my $response = $self->makeRequest('POST', $uri, $query, $payload, $headers, \%params);
    return $response;
}

# Generated code for the endpoint

# Do not change this code

# name: in query

# repositoryOwner: in path

# tag: in path

# repositoryName: in path

sub getReleaseByTagName {
    my ($self, %params) = @_;

    my $override = $self->can("getReleaseByTagNameOverride");
    if ($override) {
        return $self->$override(%params);
    }

    my $uri = "/repos/{{repositoryOwner}}/{{repositoryName}}/releases/tags/{{tag}}";
    $uri = renderOneLineTemplate($uri, %params);
    my $query = {

        'name' => $params{ name },

    };

    # TODO handle credentials
    my $payload = {

    };
    logDebug($payload);

    $self->{method} = 'getReleaseByTagName';

    my $headers = {
    };

    # Creating a request object
    my $response = $self->makeRequest('GET', $uri, $query, $payload, $headers, \%params);
    return $response;
}

# Generated code for the endpoint

# Do not change this code

# username: in path

sub getUser {
    my ($self, %params) = @_;

    my $override = $self->can("getUserOverride");
    if ($override) {
        return $self->$override(%params);
    }

    my $uri = "/users/{{username}}";
    $uri = renderOneLineTemplate($uri, %params);
    my $query = {

    };

    # TODO handle credentials
    my $payload = {

    };
    logDebug($payload);

    $self->{method} = 'getUser';

    my $headers = {
    };

    # Creating a request object
    my $response = $self->makeRequest('GET', $uri, $query, $payload, $headers, \%params);
    return $response;
}

# Generated code for the endpoint

# Do not change this code

# repositoryOwner: in path

# repositoryName: in path

# tag_name: in body

# name: in body

sub createRelease {
    my ($self, %params) = @_;

    my $override = $self->can("createReleaseOverride");
    if ($override) {
        return $self->$override(%params);
    }

    my $uri = "/repos/{{repositoryOwner}}/{{repositoryName}}/releases";
    $uri = renderOneLineTemplate($uri, %params);
    my $query = {

    };

    # TODO handle credentials
    my $payload = {

        'tag_name' => $params{ tag_name },

        'name' => $params{ name },

    };
    logDebug($payload);

    $self->{method} = 'createRelease';

    my $headers = {
    };

    $headers->{'Content-Type'} = 'application/json';

    # Creating a request object
    my $response = $self->makeRequest('POST', $uri, $query, $payload, $headers, \%params);
    return $response;
}
## === rest client ends, checksum: a683e985f9e25efbc5c601116bb5d0bc ===
=pod

Use ths method to change HTTP::Request object before the request, e.g.

    $r->header('Authorization', $myCustomAuthHeader);

If you are using custom authorization, it can be placed in here.

=cut

sub augmentRequest {
    my ($self, $r, $params) = @_;
    # empty, for user to fill
    if ($self->method eq 'uploadReleaseAsset') {
        $r = $self->_uploadReleaseAsset($r, $params);
    }
    return $r;
}

sub _uploadReleaseAsset {
    my ($self, $request, $params) = @_;

    my $assetPath = $params->{assetPath} or die "No asset path was provided";
    my $length = -s $assetPath;

    # POST https://uploads.github.com/repos/octocat/Hello-World/releases/1/assets?name=foo.zip
    open my $fh, $assetPath or die "Cannot open $assetPath: $!";
    binmode $fh;
    my $content = join '' => <$fh>;
    close $fh;
    $request->content($content);
    $request->header('Content-Length', $length);
    my $uri = $request->uri;
    $uri->host('uploads.github.com');
    return $request;
}

=pod

Use this method to override default payload encoding.
By default the payload is encoded as JSON.

=cut

sub encodePayload {
    my ($self, $payload) = @_;

    return encode_json($payload);
}

=pod

Use this method to change process response logic.

=cut

sub processResponse {
    my ($self, $response) = @_;

    return undef;
}

=pod

Use this method to override default response decoding logic.
By default the response is decoded as JSON.

=cut

sub parseResponse {
    my ($self, $response) = @_;

    if ($response->content) {
        return decode_json($response->content);
    }
}

1;
