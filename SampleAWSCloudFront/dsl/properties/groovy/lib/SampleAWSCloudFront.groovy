import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudfront.AmazonCloudFront
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder
import com.amazonaws.services.cloudfront.model.*
import com.cloudbees.flowpdf.FlowPlugin
import com.cloudbees.flowpdf.StepParameters
import com.cloudbees.flowpdf.StepResult
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
        log.info("invalidateCache was invoked with StepParameters", p)

        def credential = p.getRequiredCredential('credential')
        CloudFrontPlugin cfPlugin = new CloudFrontPlugin(credential.getUserName(), credential.getSecretValue())

        String distributionId = p.getRequiredParameter('distributionId').getValue()
        List paths = p.getRequiredParameter('objectPaths').getValue().split(/\n+/).collect { it }

        String callerReference = p.getParameter('callerReference').getValue() as String
        if (!callerReference) {
            if (!(p.getParameter('uniqueCallerReference').getValue().toString() == 'true')) {
                throw new UnexpectedMissingValue("Either 'Caller Reference' or 'Generate Unique Caller Reference' should be specified.")
            } else {
                callerReference = "Auto Generated Reference at " + new Date()
            }
        }

        try {
            cfPlugin.invalidateCache(distributionId, paths, callerReference)
            sr.setJobStepOutcome('success')
            sr.setJobStepSummary('Invalidated.')
        } catch (Exception exception) {
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary(exception.getMessage())
            log.debug(exception.getMessage(), exception.getStackTrace())
        }

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
