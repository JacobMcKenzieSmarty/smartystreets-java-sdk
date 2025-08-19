# SmartyStreets Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/com.smartystreets.api/smartystreets-java-sdk)](https://central.sonatype.com/artifact/com.smartystreets.api/smartystreets-java-sdk)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The official client library for accessing SmartyStreets APIs from Java and JVM-based languages. This SDK provides a simple, intuitive interface for address validation, geocoding, enrichment, and autocomplete services.

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Authentication](#authentication)
- [Supported APIs](#supported-apis)
- [Usage Examples](#usage-examples)
- [Configuration](#configuration)
- [Error Handling](#error-handling)
- [Development](#development)
- [License](#license)

## Requirements

- **Java 11 or later** (Java 8 supported in versions ≤ 3.10.7)
- Maven or Gradle for dependency management

## Installation

### Maven

```xml
<dependency>
    <groupId>com.smartystreets.api</groupId>
    <artifactId>smartystreets-java-sdk</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.smartystreets.api:smartystreets-java-sdk:LATEST_VERSION'
```

### Android Projects

When using this library in an Android project, exclude conflicting dependencies: 

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

## Quick Start

```java
import com.smartystreets.api.*;
import com.smartystreets.api.us_street.*;

// Initialize client with authentication
ClientBuilder clientBuilder = new ClientBuilder("your-auth-id", "your-auth-token");
Client client = clientBuilder.buildUsStreetApiClient();

// Create and send a lookup
Lookup lookup = new Lookup();
lookup.setStreet("1600 Amphitheatre Pkwy");
lookup.setCity("Mountain View");
lookup.setState("CA");

client.send(lookup);

// Get results
List<Candidate> results = lookup.getResult();
if (!results.isEmpty()) {
    Candidate candidate = results.get(0);
    System.out.println("Validated Address: " + candidate.getDeliveryLine1());
    System.out.println("ZIP Code: " + candidate.getComponents().getZipCode());
}
```

## Authentication

SmartyStreets supports two types of authentication:

### Server-to-Server (StaticCredentials)

For server-side applications using Auth ID and Auth Token:

```java
StaticCredentials credentials = new StaticCredentials("your-auth-id", "your-auth-token");
ClientBuilder clientBuilder = new ClientBuilder(credentials);
```

### Client-Side (SharedCredentials)

For browser/mobile applications using website key and hostname:

```java
SharedCredentials credentials = new SharedCredentials("your-website-key", "your-hostname");
ClientBuilder clientBuilder = new ClientBuilder(credentials);
```

**Security Note**: Store credentials in environment variables:

```java
StaticCredentials credentials = new StaticCredentials(
    System.getenv("SMARTY_AUTH_ID"),
    System.getenv("SMARTY_AUTH_TOKEN")
);
```

## Supported APIs

| API | Description | Client Method |
|-----|-------------|---------------|
| **US Street Address** | Validate and standardize US addresses | `buildUsStreetApiClient()` |
| **US ZIP Code** | Lookup city/state by ZIP code, validate ZIP codes | `buildUsZipCodeApiClient()` |
| **US Autocomplete Pro** | Real-time address autocomplete suggestions | `buildUsAutocompleteProApiClient()` |
| **US Extract** | Extract and validate addresses from unstructured text | `buildUsExtractApiClient()` |
| **US Reverse Geocoding** | Find addresses near geographic coordinates | `buildUsReverseGeoClient()` |
| **US Enrichment** | Property data, demographics, and risk information | `buildUsEnrichmentClient()` |
| **International Street** | Validate international addresses (240+ countries) | `buildInternationalStreetApiClient()` |
| **International Autocomplete** | International address autocomplete | `buildInternationalAutcompleteApiClient()` |

## Usage Examples

### US Street Address Validation

```java
import com.smartystreets.api.*;
import com.smartystreets.api.us_street.*;

ClientBuilder builder = new ClientBuilder("auth-id", "auth-token");
Client client = builder.buildUsStreetApiClient();

Lookup lookup = new Lookup();
lookup.setStreet("1600 Amphitheatre Pkwy");
lookup.setCity("Mountain View");
lookup.setState("CA");
lookup.setZipCode("94043");
lookup.setMaxCandidates(1);
lookup.setMatch(MatchType.STRICT); // Only return exact matches

client.send(lookup);

List<Candidate> candidates = lookup.getResult();
if (!candidates.isEmpty()) {
    Candidate result = candidates.get(0);
    System.out.println("Delivery Line: " + result.getDeliveryLine1());
    System.out.println("ZIP+4: " + result.getComponents().getPlus4Code());
    System.out.println("Coordinates: " + result.getMetadata().getLatitude() + 
                      ", " + result.getMetadata().getLongitude());
}
```

### Batch Processing (Up to 100 addresses)

```java
Batch batch = new Batch();

for (String address : addressList) {
    Lookup lookup = new Lookup();
    lookup.setStreet(address);
    // Set other fields...
    batch.add(lookup);
}

client.send(batch);

// Process results
for (int i = 0; i < batch.size(); i++) {
    Lookup lookup = batch.get(i);
    List<Candidate> results = lookup.getResult();
    // Handle results...
}
```

### US Enrichment API

```java
import com.smartystreets.api.us_enrichment.*;
import com.smartystreets.api.us_enrichment.lookup_types.property_principal.PropertyPrincipalLookup;

Client client = new ClientBuilder(credentials).buildUsEnrichmentClient();

// Lookup by SmartyKey
PropertyPrincipalLookup lookup = new PropertyPrincipalLookup();
lookup.setSmartyKey("your-smarty-key");
lookup.setInclude("financial_history,sale_date");

PrincipalResponse[] results = client.sendPropertyPrincipal(lookup);

// Lookup by address
lookup.setAddressSearch(
    new AddressSearch()
        .withStreet("1 E Main St")
        .withCity("Mesa")
        .withState("AZ")
        .withZipcode("85201")
);
```

### International Address Validation

```java
import com.smartystreets.api.international_street.*;

Client client = new ClientBuilder(credentials).buildInternationalStreetApiClient();

Lookup lookup = new Lookup("Rua Padre Antonio D'Angelo 121", "Brazil");
lookup.setLocality("Sao Paulo");
lookup.setAdministrativeArea("SP");
lookup.setPostalCode("02516-050");
lookup.setGeocode(true); // Include lat/lng coordinates

Candidate[] results = client.send(lookup);
if (results.length > 0) {
    Candidate result = results[0];
    System.out.println("Status: " + result.getAnalysis().getVerificationStatus());
    System.out.println("Address: " + result.getAddress1());
    System.out.println("Coordinates: " + result.getMetadata().getLatitude() + 
                      ", " + result.getMetadata().getLongitude());
}
```

### US Autocomplete Pro

```java
import com.smartystreets.api.us_autocomplete_pro.*;

Client client = new ClientBuilder(credentials).buildUsAutocompleteProApiClient();

Lookup lookup = new Lookup("1600 Amphi");
lookup.setMaxSuggestions(10);
lookup.setCityFilter("Mountain View");
lookup.setStateFilter("CA");

client.send(lookup);

List<Suggestion> suggestions = lookup.getResult();
for (Suggestion suggestion : suggestions) {
    System.out.println(suggestion.getStreetLine() + " " + 
                      suggestion.getCity() + ", " + suggestion.getState());
}
```

## Configuration

### ClientBuilder Options

The `ClientBuilder` class provides chainable methods for customization:

```java
Client client = new ClientBuilder(credentials)
    .retryAtMost(3)                    // Max retry attempts (default: 5)
    .withMaxTimeout(15000)             // Timeout in milliseconds (default: 10000)
    .withCustomBaseUrl("https://custom-endpoint.com")  // Custom API endpoint
    .withProxy(Proxy.Type.HTTP, "proxy.company.com", 8080)  // Proxy configuration
    .withCustomHeaders(headerMap)      // Additional HTTP headers
    .withDebug()                      // Enable request/response logging
    .withLicenses(Arrays.asList("us-core-cloud"))  // Specify license tracks
    .buildUsStreetApiClient();
```

### Proxy Configuration

For environments requiring proxy servers:

```java
ClientBuilder builder = new ClientBuilder(credentials)
    .withProxy(Proxy.Type.HTTP, "proxy.company.com", 8080);
```

### Custom Headers

Add custom HTTP headers (e.g., for tracking):

```java
Map<String, Object> headers = new HashMap<>();
headers.put("X-Custom-Header", "my-value");
headers.put("X-Request-ID", UUID.randomUUID().toString());

ClientBuilder builder = new ClientBuilder(credentials)
    .withCustomHeaders(headers);
```

### Debug Mode

Enable detailed request/response logging:

```java
ClientBuilder builder = new ClientBuilder(credentials)
    .withDebug();
```

## Error Handling

The SDK uses specific exception types for different error conditions:

```java
import com.smartystreets.api.exceptions.*;

try {
    client.send(lookup);
} catch (BadCredentialsException e) {
    // Invalid or missing authentication
    System.err.println("Authentication failed: " + e.getMessage());
} catch (PaymentRequiredException e) {
    // Insufficient credits
    System.err.println("Payment required: " + e.getMessage());
} catch (RequestEntityTooLargeException e) {
    // Request too large (batch size, etc.)
    System.err.println("Request too large: " + e.getMessage());
} catch (BadRequestException e) {
    // Invalid request parameters
    System.err.println("Bad request: " + e.getMessage());
} catch (InternalServerErrorException e) {
    // SmartyStreets service error
    System.err.println("Service error: " + e.getMessage());
} catch (ServiceUnavailableException e) {
    // Service temporarily unavailable
    System.err.println("Service unavailable: " + e.getMessage());
} catch (GatewayTimeoutException e) {
    // Request timeout
    System.err.println("Request timeout: " + e.getMessage());
} catch (SmartyException e) {
    // General SDK exception
    System.err.println("SDK error: " + e.getMessage());
} catch (IOException e) {
    // Network/connection error
    System.err.println("Network error: " + e.getMessage());
} catch (InterruptedException e) {
    // Thread interruption
    Thread.currentThread().interrupt();
    System.err.println("Request interrupted");
}
```

### Common Response Codes

| Code | Exception | Description |
|------|-----------|-------------|
| 401 | `BadCredentialsException` | Invalid authentication credentials |
| 402 | `PaymentRequiredException` | Insufficient account credits |
| 413 | `RequestEntityTooLargeException` | Request payload too large |
| 400 | `BadRequestException` | Invalid request parameters |
| 422 | `UnprocessableEntityException` | Missing required fields |
| 500 | `InternalServerErrorException` | SmartyStreets server error |
| 503 | `ServiceUnavailableException` | Service temporarily down |
| 504 | `GatewayTimeoutException` | Request timeout |

## Development

### Building from Source

```bash
git clone https://github.com/smartystreets/smartystreets-java-sdk.git
cd smartystreets-java-sdk
mvn clean compile
```

### Running Tests

```bash
# Unit tests
mvn test

# Integration tests (requires valid credentials)
export SMARTY_AUTH_ID=your-auth-id
export SMARTY_AUTH_TOKEN=your-auth-token
mvn verify
```

### Examples

Complete working examples are available in the [`src/main/java/examples/`](src/main/java/examples/) directory:

- [`UsStreetSingleAddressExample.java`](src/main/java/examples/UsStreetSingleAddressExample.java)
- [`UsStreetMultipleAddressesExample.java`](src/main/java/examples/UsStreetMultipleAddressesExample.java)
- [`UsEnrichmentExample.java`](src/main/java/examples/UsEnrichmentExample.java)
- [`InternationalExample.java`](src/main/java/examples/InternationalExample.java)
- [`UsAutocompleteProExample.java`](src/main/java/examples/UsAutocompleteProExample.java)
- [`UsZipCodeSingleLookupExample.java`](src/main/java/examples/UsZipCodeSingleLookupExample.java)
- [`UsExtractExample.java`](src/main/java/examples/UsExtractExample.java)
- [`UsReverseGeoExample.java`](src/main/java/examples/UsReverseGeoExample.java)

### Architecture

The SDK follows a clean architecture pattern:

- **ClientBuilder**: Factory for creating API clients with configuration
- **Client**: API-specific client classes (e.g., `us_street.Client`)
- **Lookup**: Input objects containing request parameters
- **Response Objects**: Typed response objects (e.g., `Candidate`, `Result`)
- **Sender Chain**: Modular request/response processing pipeline
- **Serializer**: JSON serialization/deserialization handling

## Resources

- **Official Documentation**: [smartystreets.com/docs/sdk/java](https://smartystreets.com/docs/sdk/java)
- **API Documentation**: [smartystreets.com/docs](https://smartystreets.com/docs)
- **Changelog**: [github.com/smartystreets/changelog/blob/master/sdk/java.md](https://github.com/smartystreets/changelog/blob/master/sdk/java.md)
- **Support**: [smartystreets.com/support](https://smartystreets.com/support)
- **GitHub Issues**: [github.com/smartystreets/smartystreets-java-sdk/issues](https://github.com/smartystreets/smartystreets-java-sdk/issues)

## License

This SDK is distributed under the [Apache License 2.0](src/main/resources/LICENSE.md).

---

**SMARTY DISCLAIMER**: Subject to the terms of the associated license agreement, this software is freely available for your use. This software is FREE, AS IN PUPPIES, and is a gift. Enjoy your new responsibility. This means that while we may consider enhancement requests, we may or may not choose to entertain requests at our sole and absolute discretion.
