import com.amazonaws.auth.*
import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudfront.*
import com.amazonaws.services.cloudfront.model.*
import com.cloudbees.flowpdf.*
import com.cloudbees.flowpdf.exceptions.UnexpectedMissingValue

/**
 * SampleAWSCloudFront
 */
class SampleAWSCloudFront extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName         : '@PLUGIN_KEY@',
                pluginVersion      : '@PLUGIN_VERSION@',
                configFields       : ['config'],
                configLocations    : ['ec_plugin_cfgs'],
                defaultConfigValues: [:]
        ]
    }

    /**
     * invalidateCache - Invalidate Cache/Invalidate Cache
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param distributionId (required: true)
     * @param objectPaths (required: true)
     * @param uniqueCallerReference (required: false)
     * @param callerReference (required: false)
     */
    def invalidateCache(StepParameters p, StepResult sr) {

        /** Retrieve the credential and initialize the client */
        def credential = p.getRequiredCredential('credential')
        CloudFrontPlugin cfPlugin = new CloudFrontPlugin(credential.getUserName(), credential.getSecretValue())

        /** Get and process the parameters */
        String distributionId = p.getRequiredParameter('distributionId').getValue()
        List paths = p.getRequiredParameter('objectPaths').getValue().split(/\n+/).collect { it }
        String callerReference = p.getParameter('callerReference').getValue() as String

        if (!callerReference) {
            if (p.getParameter('uniqueCallerReference').getValue().toString() == 'true') {
                callerReference = "Auto Generated Reference at " + new Date()
            } else {
                /** Raising exception */
                throw new UnexpectedMissingValue("Either 'Caller Reference' or 'Generate Unique Caller Reference' should be specified.")
            }
        }

        /** Call the client method to make the invalidation */
        def invalidation = cfPlugin.invalidateCache(distributionId, paths, callerReference)

        sr.setOutputParameter('invalidationId', invalidation.id)
        sr.setPipelineSummary("Invalidation Id", invalidation.id)
        sr.setJobSummary("Created invalidation Id is : ${invalidation.id}")
        sr.setJobStepSummary("Created invalidation Id is : ${invalidation.id}")

        sr.apply()

        log.info("step Invalidate Cache has been finished")
    }

// === step ends ===

}


class CloudFrontPlugin {
    AmazonCloudFront client

    CloudFrontPlugin(String username, String password) {

        def credential = new BasicAWSCredentials(username, password)
        AWSCredentialsProvider credentialProvider = new AWSStaticCredentialsProvider(credential)
        client = AmazonCloudFrontClientBuilder
                .standard()
                .withRegion(Regions.DEFAULT_REGION)
                .withCredentials(credentialProvider)
                .build()
    }


    def invalidateCache(String distributionId, List paths, String callerReference) {
        Paths p = new Paths().withItems(paths).withQuantity(paths.size())
        InvalidationBatch batch = new InvalidationBatch().withPaths(p).withCallerReference(callerReference)
        CreateInvalidationResult result = client.createInvalidation(
                new CreateInvalidationRequest()
                        .withDistributionId(distributionId)
                        .withInvalidationBatch(batch)
        )

        Invalidation invalidation = result.invalidation
        println "Invalidation ID: ${invalidation.id}"
        println "Invalidation Status: ${invalidation.status}"
        if ('Completed' != invalidation.status) {
            print "Waiting for the invalidation to complete"
        }
        while ('Completed' != invalidation.status) {
            sleep(1000 * 5)
            invalidation = getInvalidation(distributionId, invalidation.id)
            print "."
        }

        return invalidation
    }

    Invalidation getInvalidation(String distributionId, String invalidationId) {
        GetInvalidationResult result = client.getInvalidation(
                new GetInvalidationRequest()
                        .withDistributionId(distributionId)
                        .withId(invalidationId)
        )
        return result.invalidation
    }
}
