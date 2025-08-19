#### SMARTY DISCLAIMER: Subject to the terms of the associated license agreement, this software is freely available for your use. This software is FREE, AS IN PUPPIES, and is a gift. Enjoy your new responsibility. This means that while we may consider enhancement requests, we may or may not choose to entertain requests at our sole and absolute discretion.

# SmartyStreets Java SDK

The official Java client library for accessing SmartyStreets APIs, providing address validation, autocomplete, geocoding, and enrichment services.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.smartystreets.api/smartystreets-java-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.smartystreets.api/smartystreets-java-sdk)

## Compatibility

- **Java 11+**: Fully supported
- **Java 8-10**: Supported in SDK versions 3.10.7 and earlier
- **Android**: Compatible (see [Android Integration](#android-integration) for important notes)

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [Authentication](#authentication)
- [Supported APIs](#supported-apis)
- [Core Concepts](#core-concepts)
- [Usage Examples](#usage-examples)
- [Configuration](#configuration)
- [Exception Handling](#exception-handling)
- [Android Integration](#android-integration)
- [Contributing](#contributing)
- [License](#license)

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.smartystreets.api</groupId>
    <artifactId>smartystreets-java-sdk</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```gradle
implementation 'com.smartystreets.api:smartystreets-java-sdk:LATEST_VERSION'
```

## Quick Start

```java
import com.smartystreets.api.*;
import com.smartystreets.api.us_street.*;

// Set up credentials
StaticCredentials credentials = new StaticCredentials("YOUR_AUTH_ID", "YOUR_AUTH_TOKEN");

// Build client
Client client = new ClientBuilder(credentials)
    .buildUsStreetApiClient();

// Create lookup
Lookup lookup = new Lookup();
lookup.setStreet("1600 Amphitheatre Pkwy");
lookup.setCity("Mountain View");
lookup.setState("CA");
lookup.setZipCode("94043");

// Send request
client.send(lookup);

// Process results
List<Candidate> results = lookup.getResult();
if (!results.isEmpty()) {
    Candidate address = results.get(0);
    System.out.println("Validated: " + address.getDeliveryLine1());
    System.out.println("ZIP+4: " + address.getComponents().getZipCode() + "-" + address.getComponents().getPlus4Code());
}
```

## Authentication

The SDK supports two authentication methods:

### Server-to-Server (StaticCredentials)

For backend applications and server-side integrations:

```java
StaticCredentials credentials = new StaticCredentials("YOUR_AUTH_ID", "YOUR_AUTH_TOKEN");
```

**Best Practice**: Store credentials in environment variables:

```java
StaticCredentials credentials = new StaticCredentials(
    System.getenv("SMARTY_AUTH_ID"),
    System.getenv("SMARTY_AUTH_TOKEN")
);
```

### Client-Side (SharedCredentials)

For browser and mobile applications:

```java
SharedCredentials credentials = new SharedCredentials("YOUR_WEBSITE_KEY", "YOUR_DOMAIN");
```

```java
SharedCredentials credentials = new SharedCredentials(
    System.getenv("SMARTY_AUTH_WEB"),
    System.getenv("SMARTY_AUTH_REFERER")
);
```

## Supported APIs

| API Service | Description | Client Method |
|-------------|-------------|---------------|
| **US Street Address** | Validates and standardizes US addresses | `buildUsStreetApiClient()` |
| **US ZIP Code** | Validates ZIP codes and returns geographic data | `buildUsZipCodeApiClient()` |
| **US Autocomplete Pro** | Provides address suggestions as users type | `buildUsAutocompleteProApiClient()` |
| **US Reverse Geo** | Finds addresses from coordinates | `buildUsReverseGeoClient()` |
| **US Extract** | Extracts addresses from unstructured text | `buildUsExtractApiClient()` |
| **US Enrichment** | Property data, financials, risk assessment | `buildUsEnrichmentClient()` |
| **International Street** | Validates international addresses | `buildInternationalStreetApiClient()` |
| **International Autocomplete** | International address suggestions | `buildInternationalAutcompleteApiClient()` |

## Core Concepts

### ClientBuilder Pattern

The SDK uses a builder pattern for flexible client configuration:

```java
Client client = new ClientBuilder(credentials)
    .retryAtMost(3)                    // Custom retry count (default: 5)
    .withMaxTimeout(15000)             // Timeout in milliseconds (default: 10000)
    .withCustomBaseUrl("https://...")  // Custom API endpoint
    .withProxy(Proxy.Type.HTTP, "proxy.company.com", 8080)
    .withDebug()                       // Enable request/response logging
    .buildUsStreetApiClient();
```

### Lookup Objects

Each API uses specific lookup objects to configure requests:

- Input parameters (address components, search criteria)
- Processing options (match strategy, result limits)
- Custom identifiers for tracking

### Result Processing

Results are automatically parsed and stored in the lookup object:

```java
client.send(lookup);
List<Candidate> results = lookup.getResult(); // or .getResults() for some APIs
```

## Usage Examples

### US Street Address Validation

```java
import com.smartystreets.api.us_street.*;

Lookup lookup = new Lookup();
lookup.setStreet("1600 Amphitheatre Pkwy");
lookup.setCity("Mountain View");
lookup.setState("CA");
lookup.setMaxCandidates(5);
lookup.setMatch(MatchType.STRICT); // Only return valid addresses

client.send(lookup);

for (Candidate candidate : lookup.getResult()) {
    System.out.println("Delivery Line: " + candidate.getDeliveryLine1());
    System.out.println("ZIP+4: " + candidate.getComponents().getZipCode() + 
                       "-" + candidate.getComponents().getPlus4Code());
    System.out.println("Latitude: " + candidate.getMetadata().getLatitude());
    System.out.println("Longitude: " + candidate.getMetadata().getLongitude());
}
```

### US Autocomplete Pro

```java
import com.smartystreets.api.us_autocomplete_pro.*;

Lookup lookup = new Lookup("1042 W Center");
lookup.setMaxResults(10);
lookup.addCityFilter("Denver,Aurora,CO");
lookup.setPreferRatio(75); // Prefer filtered results

client.send(lookup);

for (Suggestion suggestion : lookup.getResult()) {
    System.out.printf("%s %s, %s %s%n",
        suggestion.getStreetLine(),
        suggestion.getCity(),
        suggestion.getState(),
        suggestion.getZipcode()
    );
}
```

### US Enrichment (Property Data)

```java
import com.smartystreets.api.us_enrichment.*;
import com.smartystreets.api.us_enrichment.lookup_types.property_principal.*;
import com.smartystreets.api.us_enrichment.result_types.property_principal.*;

// Using SmartyKey (recommended)
PropertyPrincipalLookup lookup = new PropertyPrincipalLookup();
lookup.setSmartyKey("1682393594");
lookup.setInclude("group_structural,sale_date");

// Or using address components
// lookup.setAddressSearch(new AddressSearch()
//     .withStreet("1600 Amphitheatre Pkwy")
//     .withCity("Mountain View")
//     .withState("CA")
//     .withZipcode("94043"));

PrincipalResponse[] results = client.sendPropertyPrincipal(lookup);

if (results != null && results.length > 0) {
    PrincipalResponse property = results[0];
    System.out.println("Property Type: " + property.getAttributes().getBuildingType());
    System.out.println("Year Built: " + property.getAttributes().getYearBuilt());
    System.out.println("Square Footage: " + property.getAttributes().getTotalMarketArea());
}
```

### International Address Validation

```java
import com.smartystreets.api.international_street.*;

Lookup lookup = new Lookup("Rua Padre Antonio D'Angelo 121 Casa Verde, Sao Paulo", "Brazil");
lookup.setGeocode(true); // Include coordinates

Candidate[] candidates = client.send(lookup);

if (candidates.length > 0) {
    Candidate address = candidates[0];
    System.out.println("Status: " + address.getAnalysis().getVerificationStatus());
    System.out.println("Formatted Address:");
    System.out.println("  " + address.getAddress1());
    System.out.println("  " + address.getAddress2());
    System.out.println("  " + address.getAddress3());
    System.out.println("  " + address.getAddress4());
    
    if (address.getMetadata() != null) {
        System.out.println("Coordinates: " + 
            address.getMetadata().getLatitude() + ", " +
            address.getMetadata().getLongitude());
    }
}
```

### Batch Processing

Some APIs support batch processing for efficiency:

```java
import com.smartystreets.api.us_street.*;

Batch batch = new Batch();

Lookup lookup1 = new Lookup();
lookup1.setStreet("1600 Amphitheatre Pkwy");
lookup1.setCity("Mountain View");
lookup1.setState("CA");

Lookup lookup2 = new Lookup();
lookup2.setStreet("1 E Main St");
lookup2.setCity("Mesa");
lookup2.setState("AZ");

batch.add(lookup1);
batch.add(lookup2);

client.send(batch);

// Process results from each lookup
for (int i = 0; i < batch.size(); i++) {
    Lookup lookup = batch.get(i);
    System.out.println("Results for lookup " + i + ":");
    for (Candidate candidate : lookup.getResult()) {
        System.out.println("  " + candidate.getDeliveryLine1());
    }
}
```

## Configuration

### Custom Headers

Add custom headers for specialized requirements:

```java
Map<String, Object> headers = new HashMap<>();
headers.put("User-Agent", "MyApp/1.0");
headers.put("X-Custom-Header", "custom-value");

Client client = new ClientBuilder(credentials)
    .withCustomHeaders(headers)
    .buildUsStreetApiClient();
```

### Proxy Configuration

For environments requiring proxy servers:

```java
Client client = new ClientBuilder(credentials)
    .withProxy(Proxy.Type.HTTP, "proxy.company.com", 8080)
    .buildUsStreetApiClient();
```

### Custom Base URL

For on-premise or custom SmartyStreets installations:

```java
Client client = new ClientBuilder(credentials)
    .withCustomBaseUrl("https://api.internal.company.com/street-address")
    .buildUsStreetApiClient();
```

### License Management

Specify which SmartyStreets licenses (tracks) to use:

```java
List<String> licenses = Arrays.asList("us-core-cloud", "us-rooftop-geo-cloud");

Client client = new ClientBuilder(credentials)
    .withLicenses(licenses)
    .buildUsStreetApiClient();
```

### Debug Mode

Enable detailed HTTP request/response logging:

```java
Client client = new ClientBuilder(credentials)
    .withDebug()
    .buildUsStreetApiClient();
```

## Exception Handling

The SDK provides specific exceptions for different error conditions:

```java
import com.smartystreets.api.exceptions.*;

try {
    client.send(lookup);
} catch (BadCredentialsException e) {
    System.err.println("Invalid authentication credentials");
} catch (PaymentRequiredException e) {
    System.err.println("Insufficient credits or subscription required");
} catch (RequestEntityTooLargeException e) {
    System.err.println("Request payload too large");
} catch (TooManyRequestsException e) {
    System.err.println("Rate limit exceeded, retry after delay");
} catch (InternalServerErrorException e) {
    System.err.println("Server error, try again later");
} catch (ServiceUnavailableException e) {
    System.err.println("Service temporarily unavailable");
} catch (SmartyException e) {
    System.err.println("General API error: " + e.getMessage());
} catch (IOException e) {
    System.err.println("Network error: " + e.getMessage());
} catch (InterruptedException e) {
    System.err.println("Request interrupted");
    Thread.currentThread().interrupt();
}
```

### HTTP Status Code Mapping

| Status Code | Exception | Description |
|-------------|-----------|-------------|
| 400 | `BadRequestException` | Invalid request parameters |
| 401 | `BadCredentialsException` | Authentication failed |
| 402 | `PaymentRequiredException` | Insufficient credits |
| 403 | `ForbiddenException` | Access denied |
| 413 | `RequestEntityTooLargeException` | Payload too large |
| 422 | `UnprocessableEntityException` | Invalid data format |
| 429 | `TooManyRequestsException` | Rate limit exceeded |
| 500 | `InternalServerErrorException` | Server error |
| 503 | `ServiceUnavailableException` | Service unavailable |
| 504 | `GatewayTimeoutException` | Request timeout |

## Android Integration

When using this SDK in Android projects, exclude conflicting dependencies:

### Maven

```xml
<dependency>
    <groupId>com.smartystreets.api</groupId>
    <artifactId>smartystreets-java-sdk</artifactId>
    <version>LATEST_VERSION</version>
    <exclusions>
        <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### Gradle

```gradle
implementation('com.smartystreets.api:smartystreets-java-sdk:LATEST_VERSION') {
    exclude group: 'commons-logging', module: 'commons-logging'
    exclude group: 'org.apache.httpcomponents', module: 'httpclient'
}
```

## Best Practices

### 1. Credential Management
- Store credentials in environment variables
- Never hard-code credentials in source code
- Use server-side credentials for backend applications
- Use website keys for client-side applications

### 2. Error Handling
- Always wrap API calls in try-catch blocks
- Handle specific exceptions appropriately
- Implement retry logic for transient errors
- Log errors for debugging and monitoring

### 3. Performance Optimization
- Use batch processing when available
- Implement connection pooling for high-volume applications
- Cache results when appropriate
- Configure appropriate timeouts

### 4. Rate Limiting
- Monitor your API usage
- Implement exponential backoff for rate limit errors
- Consider spreading requests over time for large batches

## Example Applications

Complete example applications are available in the [`src/main/java/examples`](src/main/java/examples) directory:

- **[UsStreetSingleAddressExample.java](src/main/java/examples/UsStreetSingleAddressExample.java)** - Basic address validation
- **[UsStreetMultipleAddressesExample.java](src/main/java/examples/UsStreetMultipleAddressesExample.java)** - Batch processing
- **[UsAutocompleteProExample.java](src/main/java/examples/UsAutocompleteProExample.java)** - Address autocomplete
- **[UsEnrichmentExample.java](src/main/java/examples/UsEnrichmentExample.java)** - Property data enrichment
- **[InternationalExample.java](src/main/java/examples/InternationalExample.java)** - International address validation
- **[UsZipCodeSingleLookupExample.java](src/main/java/examples/UsZipCodeSingleLookupExample.java)** - ZIP code validation

## API Documentation

For detailed API documentation, visit:
- [SmartyStreets Java SDK Documentation](https://smartystreets.com/docs/sdk/java)
- [US Street Address API](https://smartystreets.com/docs/us-street-api)
- [US ZIP Code API](https://smartystreets.com/docs/us-zipcode-api)
- [US Autocomplete Pro API](https://smartystreets.com/docs/cloud/us-autocomplete-pro-api)
- [International Street API](https://smartystreets.com/docs/cloud/international-street-api)

## Changelog

See the [SmartyStreets SDK Changelog](https://github.com/smartystreets/changelog/blob/master/sdk/java.md) for version history and updates.

## Contributing

We welcome contributions! Please see our [contribution guidelines](CONTRIBUTING.md) for details.

## Support

- **Documentation**: [smartystreets.com/docs](https://smartystreets.com/docs)
- **Support Portal**: [smartystreets.com/support](https://smartystreets.com/support)
- **Email**: support@smartystreets.com

## License

[Apache License 2.0](src/main/resources/LICENSE.md)

---

[![asciicast](https://asciinema.org/a/122130.png)](https://asciinema.org/a/122130)
