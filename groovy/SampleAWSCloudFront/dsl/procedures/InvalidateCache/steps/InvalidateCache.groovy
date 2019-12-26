$[/myProject/groovy/scripts/preamble.groovy.ignore]

SampleAWSCloudFront plugin = new SampleAWSCloudFront()
plugin.runStep('Invalidate Cache', 'Invalidate Cache', 'invalidateCache')