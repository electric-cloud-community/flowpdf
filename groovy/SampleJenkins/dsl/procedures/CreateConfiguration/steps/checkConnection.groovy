import com.electriccloud.client.groovy.ElectricFlow

// Sample code
ElectricFlow ef = new ElectricFlow()

try {
    String endpoint = ef.getProperty(propertyName: 'endpoint')?.property?.value
    assert endpoint == 'endpoint'
} catch (Throwable e) {
    throw new RuntimeException("Connection failed")
}
