
procedure 'Test Sample Reporting Plugin', {
  description = ''
  jobNameTemplate = ''
  projectName = 'Default'
  resourceName = ''
  timeLimit = ''
  timeLimitUnits = 'minutes'
  workspaceName = ''

  step 'InitialRetrieve', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortJob'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = 'Default'
    releaseMode = 'none'
    resourceName = 'local'
    shell = null
    subprocedure = 'CollectReportingData'
    subproject = '/plugins/SampleReporting/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'config', 'config'
    actualParameter 'debug', '1'
    actualParameter 'param1', 'sampleValue'
    actualParameter 'param2', ''
    actualParameter 'previewMode', '0'
    actualParameter 'releaseName', 'Release'
    actualParameter 'releaseProjectName', 'ReleaseProject'
    actualParameter 'transformScript', ''
  }

  step 'Show the metadata value', {
    description = ''
    alwaysRun = '1'
    broadcast = '0'
    command = 'ectool setProperty /myJobStep/summary `ectool getProperty /projects/Default/ecreport_data_tracker/SampleReporting-sampleValue-build`'
    condition = ''
    errorHandling = 'failProcedure'
    exclusiveMode = 'none'
    logFileName = ''
    parallel = '0'
    postProcessor = ''
    precondition = ''
    projectName = 'Default'
    releaseMode = 'none'
    resourceName = ''
    shell = ''
    subprocedure = null
    subproject = null
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = ''
    workspaceName = ''
  }

  step 'HaveNewRecord', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortJob'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = 'Default'
    releaseMode = 'none'
    resourceName = 'local'
    shell = null
    subprocedure = 'CollectReportingData'
    subproject = '/plugins/SampleReporting/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'config', 'config'
    actualParameter 'debug', '1'
    actualParameter 'param1', 'sampleValue'
    actualParameter 'param2', ''
    actualParameter 'previewMode', '0'
    actualParameter 'releaseName', 'Release'
    actualParameter 'releaseProjectName', 'ReleaseProject'
    actualParameter 'transformScript', ''
  }

  step 'Show the metadata value copy', {
    description = ''
    alwaysRun = '1'
    broadcast = '0'
    command = 'ectool setProperty /myJobStep/summary `ectool getProperty /projects/Default/ecreport_data_tracker/SampleReporting-sampleValue-build`'
    condition = ''
    errorHandling = 'failProcedure'
    exclusiveMode = 'none'
    logFileName = ''
    parallel = '0'
    postProcessor = ''
    precondition = ''
    projectName = 'Default'
    releaseMode = 'none'
    resourceName = ''
    shell = ''
    subprocedure = null
    subproject = null
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = ''
    workspaceName = ''
  }

  step 'No new records', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = null
    condition = ''
    errorHandling = 'abortJob'
    exclusiveMode = 'none'
    logFileName = null
    parallel = '0'
    postProcessor = null
    precondition = ''
    projectName = 'Default'
    releaseMode = 'none'
    resourceName = 'local'
    shell = null
    subprocedure = 'CollectReportingData'
    subproject = '/plugins/SampleReporting/project'
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = null
    workspaceName = ''
    actualParameter 'config', 'config'
    actualParameter 'debug', '1'
    actualParameter 'param1', 'sampleValue'
    actualParameter 'param2', ''
    actualParameter 'previewMode', '0'
    actualParameter 'releaseName', 'Release'
    actualParameter 'releaseProjectName', 'ReleaseProject'
    actualParameter 'transformScript', ''
  }

  step 'Show the metadata value copy 2', {
    description = ''
    alwaysRun = '1'
    broadcast = '0'
    command = 'ectool setProperty /myJobStep/summary `ectool getProperty /projects/Default/ecreport_data_tracker/SampleReporting-sampleValue-build`'
    condition = ''
    errorHandling = 'failProcedure'
    exclusiveMode = 'none'
    logFileName = ''
    parallel = '0'
    postProcessor = ''
    precondition = ''
    projectName = 'Default'
    releaseMode = 'none'
    resourceName = ''
    shell = ''
    subprocedure = null
    subproject = null
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = ''
    workspaceName = ''
  }

  step 'Clear the metadata', {
    description = ''
    alwaysRun = '1'
    broadcast = '0'
    command = 'ectool deleteProperty /projects/Default/ecreport_data_tracker/SampleReporting-sampleValue-build'
    condition = ''
    errorHandling = 'failProcedure'
    exclusiveMode = 'none'
    logFileName = ''
    parallel = '0'
    postProcessor = ''
    precondition = ''
    projectName = 'Default'
    releaseMode = 'none'
    resourceName = ''
    shell = ''
    subprocedure = null
    subproject = null
    timeLimit = ''
    timeLimitUnits = 'minutes'
    workingDirectory = ''
    workspaceName = ''
  }
}
