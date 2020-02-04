## Reporting

This is a sample plugin that utilizes DOIS feature of CloudBees Flow. The plugin is not using any third party connection and just generates random reporting data.

* [Reporting Module](dsl/properties/perl/lib/FlowPlugin/Reporting/Reporting.pm)
* [Main Module](dsl/properties/perl/lib/FlowPlugin/Reporting.pm)
* [Plugin Metadata](dsl/promote.groovy)
* [DOIS Configuration Forms](dsl/properties/ec_devops_insight/)

The plugin contains only one procedure, CollectReportingData. This procedure is responsible
for sending data to the reporting server.

The forms for DOIS are not generated (the contents of dsl/properties/ec_devops_insight). They
should be created manually, forms (`ec_parameterForm`) and a DSL script (`script`).

The form should contain elements for the reporting schedule, e.g.

```xml
<editor>
  <help>/commander/pages/@PLUGIN_NAME@/help?s=Administration&amp;ss=Plugins#CollectReportingData</help>
  <formElement>
    <type>entry</type>
    <property>config</property>
    <configuration>1</configuration>
    <propertyReference>/plugins/@PLUGIN_NAME@/project/ec_plugin_cfgs</propertyReference>
    <required>1</required>
  </formElement>
  <formElement>
    <type>entry</type>
    <property>test1</property>
  </formElement>
  <formElement>
    <type>entry</type>
    <property>frequency</property>
  </formElement>
</editor>
```

`script` should be a DSL  script that will create/update the Reporting schedule.
