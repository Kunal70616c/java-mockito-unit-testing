# Mockito Unit Testing for Spring Applications

A comprehensive guide to implementing unit tests with Mockito framework for mocking dependencies in a Spring Banking Application.

## Repository
**This Repository:** [GitHub - Java Mockito Unit Testing](https://github.com/Kunal70616c/java-mockito-unit-testing.git)

**Previous Testing Project:** [GitHub - JDBC JUnit Unit Testing](https://github.com/Kunal70616c/jdbc-junit-unit-testing.git)

**Base Application:** [GitHub - Spring JDBC Data Access](https://github.com/Kunal70616c/spring-jdbc-data-access.git)

> **Note:** This project builds upon the JUnit testing project by adding Mockito for mocking dependencies. All previous JUnit tests remain valid - we're adding a new dimension of testing with mocks.

## Table of Contents
- [What is Mockito?](#what-is-mockito)
- [Why Use Mockito?](#why-use-mockito)
- [Mocking vs Real Objects](#mocking-vs-real-objects)
- [Mockito Core Concepts](#mockito-core-concepts)
- [Project Structure](#project-structure)
- [Mockito Annotations](#mockito-annotations)
- [Test Implementation](#test-implementation)
- [Stubbing Methods](#stubbing-methods)
- [Verifying Interactions](#verifying-interactions)
- [Best Practices](#best-practices)
- [Running Tests](#running-tests)

---

## What is Mockito?

**Mockito** is the most popular mocking framework for Java unit testing. It allows you to create and configure mock objects (fake implementations) of your dependencies, enabling you to test classes in isolation without requiring the actual implementations.

### The Problem Mockito Solves

Consider testing a service that depends on a repository:

```java
@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository; // Depends on database
    
    public Customer addCustomer(Customer customer) {
        return customerRepository.addCustomer(customer);
    }
}
```

**Without Mockito:**
- Need a running database
- Need test data setup
- Tests are slow (database I/O)
- Tests are fragile (database state issues)
- Hard to test edge cases (database errors)

**With Mockito:**
- No database required
- Fast test execution (milliseconds)
- Complete control over dependency behavior
- Easy to simulate errors and edge cases
- True unit testing - test only the service logic

---

## Why Use Mockito?

### 1. **Isolation**
Test one class at a time without dependencies.

```java
// Test CustomerService without needing CustomerRepository implementation
@Mock
private CustomerRepository customerRepository;

@InjectMocks
private CustomerService customerService;
```

### 2. **Speed**
Mock objects execute in memory - no database, network, or file I/O.

**Performance Comparison:**
```
Real Database Test:  500ms - 2000ms
Mocked Test:        5ms - 20ms
```

### 3. **Control**
Simulate any scenario, including errors.

```java
// Simulate database failure
Mockito.when(customerRepository.addCustomer(any()))
    .thenThrow(new DataAccessException("Database unavailable"));
```

### 4. **Simplicity**
Test complex scenarios without complex setup.

```java
// No need to populate database with 1000 records
List<Customer> largeList = generateMockCustomers(1000);
Mockito.when(customerRepository.getAllCustomers()).thenReturn(largeList);
```

### 5. **Focus**
Test business logic, not infrastructure.

```java
// Test service logic, not JDBC implementation
@Test
public void testCustomerValidation() {
    // Focus on validation logic, not database interaction
}
```

---

## Mocking vs Real Objects

### When to Use Mocks

✅ **Use Mocks For:**
- External dependencies (databases, APIs, file systems)
- Slow operations
- Non-deterministic behavior (random, time-based)
- Difficult-to-reproduce scenarios (network failures)
- Testing error handling
- Dependencies not yet implemented

### When to Use Real Objects

✅ **Use Real Objects For:**
- The class being tested
- Simple value objects (Customer, FullName)
- Pure functions with no side effects
- Testing integration between components

### Example

```java
@Test
public void testAddCustomer() {
    // REAL - The object we're testing
    CustomerService customerService = new CustomerService();
    
    // REAL - Simple value object
    Customer customer = new Customer();
    customer.setAccountNo(1001);
    
    // MOCK - External dependency (database)
    CustomerRepository mockRepository = mock(CustomerRepository.class);
    
    // Use mocked repository
    when(mockRepository.addCustomer(customer)).thenReturn(customer);
}
```

---

## Mockito Core Concepts

### 1. Mock Object

A mock is a fake implementation of an interface or class that you can control.

```java
// Create a mock
CustomerRepository mockRepository = mock(CustomerRepository.class);

// By default, mocks return:
// - null for objects
// - 0 for numbers
// - false for booleans
// - empty collections
```

### 2. Stubbing

Stubbing defines what a mock should return when a method is called.

```java
// Stub a method
when(mockRepository.getAllCustomers())
    .thenReturn(Arrays.asList(customer1, customer2));

// Now calling the method returns our predefined list
List<Customer> result = mockRepository.getAllCustomers();
```

### 3. Verification

Verification checks that certain methods were called (or not called).

```java
// Call the method
customerService.deleteCustomer(1001L);

// Verify it was called
verify(mockRepository).deleteCustomer(1001L);
```

### 4. Argument Matching

Match method arguments flexibly.

```java
// Match any argument
when(mockRepository.addCustomer(any(Customer.class)))
    .thenReturn(customer);

// Match specific value
when(mockRepository.getCustomerById(1001L))
    .thenReturn(customer);
```

---

## Project Structure

```
java-mockito-unit-testing/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── sh.surge.kunal.banking/
│   │   │       ├── configurations/
│   │   │       │   └── AppConfig.java
│   │   │       ├── models/
│   │   │       │   ├── Customer.java
│   │   │       │   ├── FullName.java
│   │   │       │   └── ...
│   │   │       ├── repositories/
│   │   │       │   ├── CustomerRepository.java
│   │   │       │   └── CustomerRepositoryImpl.java
│   │   │       ├── services/
│   │   │       │   └── CustomerService.java          # Class under test
│   │   │       └── utils/
│   │   │           └── CustomerApp.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       │   └── sh.surge.kunal.banking/
│       │       ├── models/
│       │       │   └── CustomerTest.java              # JUnit tests
│       │       ├── services/
│       │       │   └── CustomerServiceTest.java       # NEW: Mockito tests
│       │       └── suites/
│       │           └── CustomerTestSuite.java
│       └── resources/
│           └── customer.csv
└── pom.xml
```

---

## Mockito Annotations

### @ExtendWith(MockitoExtension.class)

Enables Mockito annotations in JUnit 5 tests.

```java
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    // Mockito annotations now work
}
```

**What it does:**
- Initializes mocks before each test
- Validates framework usage
- Cleans up after tests
- Replaces the old `@RunWith(MockitoJUnitRunner.class)` from JUnit 4

---

### @Mock

Creates a mock instance of a class or interface.

```java
@Mock
private CustomerRepository customerRepository;
```

**Equivalent to:**
```java
private CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
```

**Key Points:**
- Mock has no real implementation
- All methods return default values (null, 0, false, empty collections)
- You must stub methods you want to return specific values
- Used for dependencies you want to fake

---

### @InjectMocks

Creates an instance and injects all `@Mock` dependencies into it.

```java
@Mock
private CustomerRepository customerRepository;

@InjectMocks
private CustomerService customerService;
```

**What happens:**
1. Mockito creates a real `CustomerService` instance
2. Injects the mocked `CustomerRepository` into it
3. Now you can test `CustomerService` with a fake repository

**Injection Methods (in order of precedence):**
1. Constructor injection (preferred)
2. Setter injection
3. Field injection

---

### @Spy

Creates a partial mock - real object with some methods mocked.

```java
@Spy
private CustomerService customerService;

@Test
public void testWithSpy() {
    // Real methods are called unless stubbed
    when(customerService.getAllCustomers()).thenReturn(mockList);
}
```

**Difference from @Mock:**
- `@Mock`: All methods return defaults
- `@Spy`: Real methods execute unless stubbed

---

### @Captor

Captures arguments passed to mocked methods.

```java
@Captor
private ArgumentCaptor<Customer> customerCaptor;

@Test
public void testArgumentCapture() {
    customerService.addCustomer(customer);
    
    verify(customerRepository).addCustomer(customerCaptor.capture());
    Customer captured = customerCaptor.getValue();
    
    assertEquals("john@example.com", captured.getEmail());
}
```

---

## Test Implementation

### CustomerServiceTest - Complete Analysis

Let's break down the comprehensive Mockito test implementation:

```java
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    
    @InjectMocks
    private CustomerService customerService;
    
    private static Customer customer;
    private static FullName fullName;
    private static Faker faker;
```

**Setup Explanation:**
1. `@ExtendWith(MockitoExtension.class)` - Enables Mockito in JUnit 5
2. `@Mock` - Creates fake repository (no database needed)
3. `@InjectMocks` - Creates real service with mocked repository injected
4. Static fields for test data shared across all tests

---

### Test Lifecycle with @BeforeAll

```java
@BeforeAll
public static void setup() {
    faker = new Faker();
    customer = new Customer();
    fullName = new FullName();
}
```

**Why @BeforeAll instead of @BeforeEach?**
- `@BeforeAll`: Runs once before all tests (must be static)
- `@BeforeEach`: Runs before each test
- Here, we initialize shared objects once for efficiency
- Each test modifies these objects, so they're reset per test anyway

---

### Test 1: Add Customer Mock Test

```java
@Test
public void addCustomerMockTest() {
    // ARRANGE - Set up test data
    customer.setAccountNo(faker.number().numberBetween(1000000000L, 9999999999L));
    customer.setFullName(fullName);
    customer.getFullName().setFirstName(faker.name().firstName());
    customer.getFullName().setMiddleName(faker.name().nameWithMiddle());
    customer.getFullName().setLastName(faker.name().lastName());
    customer.setEmail(faker.internet().emailAddress());
    customer.setContactNo(Long.parseLong(faker.phoneNumber().subscriberNumber(10)));
    customer.setPassword(faker.internet().password(8, 10, true, true, true));
    
    // ARRANGE - Stub the mock behavior
    Mockito.when(customerRepository.addCustomer(customer)).thenReturn(customer);
    
    // ACT - Execute the method being tested
    Customer result = customerService.addCustomer(customer);
    
    // ASSERT - Verify the results
    assertEquals(customer, result);
    assertEquals(result.getAccountNo(), customer.getAccountNo());
}
```

**Detailed Breakdown:**

#### 1. Test Data Setup (Arrange)
```java
customer.setAccountNo(faker.number().numberBetween(1000000000L, 9999999999L));
// ... more setters
```
- Creates realistic test data using Faker
- Each run has different data (helps catch bugs)
- Tests with realistic values, not just "test123"

#### 2. Stubbing (Arrange)
```java
Mockito.when(customerRepository.addCustomer(customer)).thenReturn(customer);
```

**Translation:** "When `addCustomer()` is called with this customer, return the same customer"

**Without this stub:**
- Mock would return `null` by default
- Test would fail with `NullPointerException`

**Flow:**
```
customerService.addCustomer(customer)
    ↓
customerRepository.addCustomer(customer)  ← INTERCEPTED by Mockito
    ↓
Returns: customer (as we stubbed)
```

#### 3. Execution (Act)
```java
Customer result = customerService.addCustomer(customer);
```
- Calls the REAL `CustomerService` method
- Which internally calls the MOCKED `CustomerRepository` method
- No database involved!

#### 4. Assertions (Assert)
```java
assertEquals(customer, result);
assertEquals(result.getAccountNo(), customer.getAccountNo());
```
- Verifies service returns what repository returned
- Tests the "pass-through" behavior of the service

**What This Test Validates:**
✅ CustomerService correctly calls CustomerRepository  
✅ CustomerService returns the result from the repository  
✅ No data transformation errors  
✅ Service layer integration works  

**What This Test DOESN'T Validate:**
❌ Database insertion (that's the repository's job)  
❌ JDBC query execution (tested separately)  
❌ Transaction management (integration test concern)  

---

### Test 2: Get All Customers Mock Test

```java
@Test
public void getAllCustomersMockTest() {
    // ARRANGE - Get real customer data
    List<Customer> customers = CustomerApp.getAllCustomers();
    
    // ARRANGE - Stub the mock
    Mockito.when(customerRepository.getAllCustomers()).thenReturn(customers);
    
    // ACT - Execute the method
    List<Customer> result = customerService.getAllCustomers();
    
    // ASSERT - Verify results
    assertEquals(customers, result);
    assertEquals(5, result.size());
    assertEquals(customers.get(0).getAccountNo(), result.get(0).getAccountNo());
}
```

**Key Points:**

#### Using Real Data from CustomerApp
```java
List<Customer> customers = CustomerApp.getAllCustomers();
```
- Leverages existing utility to generate customer list
- Realistic test data (5 customers with varied data)
- Reuses code instead of duplicating setup

#### Stubbing with No Arguments
```java
Mockito.when(customerRepository.getAllCustomers()).thenReturn(customers);
```
- Method takes no parameters
- Always returns the same list when called
- Simulates database query returning 5 customers

#### Multiple Assertions
```java
assertEquals(customers, result);           // Same list reference
assertEquals(5, result.size());            // Correct count
assertEquals(customers.get(0).getAccountNo(), 
             result.get(0).getAccountNo()); // First element matches
```

**Why Multiple Assertions?**
- List equality (`assertEquals(customers, result)`) checks size and elements
- Explicit size check documents expectation
- Element-level check ensures no transformation errors

**What This Tests:**
✅ Service retrieves all customers from repository  
✅ Service returns complete list  
✅ No data loss during retrieval  
✅ List order preserved  

---

### Test 3: Delete Customer Mock Test

```java
@Test
public void deleteCustomerMockTest() {
    // ARRANGE - Create test account number
    long accountNo = faker.number().numberBetween(1000000000L, 9999999999L);
    
    // ARRANGE - Stub the mock
    Mockito.when(customerRepository.deleteCustomer(accountNo)).thenReturn(true);
    
    // ACT - Execute the method
    boolean result = customerService.deleteCustomer(accountNo);
    
    // ASSERT - Verify result
    assertTrue(result);
}
```

**Key Points:**

#### Testing with Primitive Return Type
```java
Mockito.when(customerRepository.deleteCustomer(accountNo)).thenReturn(true);
```
- Returns `boolean` instead of object
- `true` = successful deletion
- Could also test failure case with `thenReturn(false)`

#### Simple Assertion
```java
assertTrue(result);
```
- Verifies successful deletion
- In real scenario, might also verify logging or events

**What This Tests:**
✅ Service delegates deletion to repository  
✅ Service returns repository's response  
✅ Boolean result properly propagated  

**Enhancement Opportunity:**
```java
@Test
public void deleteCustomerNotFoundTest() {
    long accountNo = 9999999999L;
    
    // Simulate customer not found
    Mockito.when(customerRepository.deleteCustomer(accountNo)).thenReturn(false);
    
    boolean result = customerService.deleteCustomer(accountNo);
    
    assertFalse(result); // Deletion failed as expected
}
```

---

## Stubbing Methods

Stubbing defines how mocks behave when their methods are called.

### Basic Stubbing

```java
// Return a value
when(mockRepository.getCustomerById(1001L)).thenReturn(customer);

// Return different values on consecutive calls
when(mockRepository.getAllCustomers())
    .thenReturn(list1)      // First call
    .thenReturn(list2);     // Second call

// Throw an exception
when(mockRepository.addCustomer(any()))
    .thenThrow(new DataAccessException("Database error"));

// Do nothing (for void methods)
doNothing().when(mockRepository).deleteCustomer(1001L);
```

---

### Argument Matchers

#### any()
Matches any argument of the specified type.

```java
when(mockRepository.addCustomer(any(Customer.class)))
    .thenReturn(customer);

// Matches ANY customer object
mockRepository.addCustomer(customer1);  // Returns customer
mockRepository.addCustomer(customer2);  // Returns customer
```

#### eq()
Matches exact value (useful when mixing matchers and values).

```java
when(mockRepository.updateCustomer(eq(1001L), any(Customer.class)))
    .thenReturn(customer);
```

#### anyLong(), anyString(), anyInt()
Matches any primitive or wrapper.

```java
when(mockRepository.getCustomerById(anyLong()))
    .thenReturn(customer);
```

#### isNull() / isNotNull()
Matches null or non-null arguments.

```java
when(mockRepository.addCustomer(isNull()))
    .thenThrow(new IllegalArgumentException());
```

---

### Advanced Stubbing

#### Custom Answer
Execute custom logic when method is called.

```java
when(mockRepository.addCustomer(any(Customer.class)))
    .thenAnswer(invocation -> {
        Customer arg = invocation.getArgument(0);
        arg.setAccountNo(12345L);
        return arg;
    });
```

#### Spy Real Methods
Call real method on spy.

```java
@Spy
private CustomerService customerService;

// Real method will be called
doCallRealMethod().when(customerService).getAllCustomers();
```

#### Conditional Stubbing
Stub based on argument values.

```java
when(mockRepository.getCustomerById(longThat(id -> id > 1000)))
    .thenReturn(customer);
```

---

## Verifying Interactions

Verification checks that mocked methods were called with expected arguments.

### Basic Verification

```java
// Verify method was called
verify(mockRepository).addCustomer(customer);

// Verify method was called with any argument
verify(mockRepository).addCustomer(any(Customer.class));

// Verify method was called with specific argument
verify(mockRepository).getCustomerById(1001L);
```

---

### Verification Modes

#### times()
Verify exact number of invocations.

```java
// Called exactly once
verify(mockRepository, times(1)).addCustomer(customer);

// Called exactly 3 times
verify(mockRepository, times(3)).getAllCustomers();
```

#### never()
Verify method was never called.

```java
verify(mockRepository, never()).deleteCustomer(anyLong());
```

#### atLeast() / atMost()
Verify minimum or maximum calls.

```java
verify(mockRepository, atLeast(1)).getAllCustomers();
verify(mockRepository, atMost(3)).addCustomer(any());
```

#### only()
Verify this was the only method called on the mock.

```java
verify(mockRepository, only()).getCustomerById(1001L);
```

---

### Verification Order

```java
// Create order verifier
InOrder inOrder = inOrder(mockRepository);

// Verify calls happened in order
inOrder.verify(mockRepository).addCustomer(customer);
inOrder.verify(mockRepository).getAllCustomers();
```

---

### Example with Verification

```java
@Test
public void testAddCustomerWithVerification() {
    // Arrange
    Customer customer = new Customer();
    customer.setAccountNo(1001L);
    when(mockRepository.addCustomer(customer)).thenReturn(customer);
    
    // Act
    customerService.addCustomer(customer);
    
    // Assert
    verify(mockRepository, times(1)).addCustomer(customer);
    verify(mockRepository, never()).deleteCustomer(anyLong());
}
```

---

## Best Practices

### 1. Mock External Dependencies Only

```java
// Good - Mock external dependency
@Mock
private CustomerRepository customerRepository;

@InjectMocks
private CustomerService customerService;

// Bad - Don't mock the class you're testing
@Mock
private CustomerService customerService; // ❌ Testing nothing!
```

---

### 2. Use Real Objects for Value Objects

```java
// Good - Real customer object
Customer customer = new Customer();
customer.setAccountNo(1001L);

// Bad - Mocking value object
@Mock
private Customer customer; // ❌ Unnecessary!
```

---

### 3. Don't Overuse Mocks

```java
// Bad - Mocking everything
@Mock private CustomerRepository repo;
@Mock private EmailService emailService;
@Mock private Logger logger;
@Mock private Validator validator;
@Mock private Formatter formatter;

// Good - Mock only what you need
@Mock private CustomerRepository repo; // External dependency
// Use real implementations for the rest
```

---

### 4. Verify Behavior, Not Implementation

```java
// Good - Verify expected behavior
@Test
public void testCustomerAdded() {
    customerService.addCustomer(customer);
    verify(mockRepository).addCustomer(customer);
}

// Bad - Over-specifying implementation
@Test
public void testCustomerAddedExactly() {
    customerService.addCustomer(customer);
    verify(mockRepository, times(1)).addCustomer(customer);
    verify(mockRepository, never()).updateCustomer(any());
    verify(mockRepository, never()).deleteCustomer(anyLong());
    // Too much implementation detail!
}
```

---

### 5. Use Argument Matchers Consistently

```java
// Bad - Mixing matchers and exact values incorrectly
when(mockRepository.updateCustomer(any(), customer))  // ❌ Won't work!

// Good - All matchers or all exact values
when(mockRepository.updateCustomer(any(Customer.class)))  // ✓
when(mockRepository.updateCustomer(customer))             // ✓
```

---

### 6. Test Both Success and Failure Cases

```java
@Test
public void testAddCustomerSuccess() {
    when(mockRepository.addCustomer(customer)).thenReturn(customer);
    Customer result = customerService.addCustomer(customer);
    assertNotNull(result);
}

@Test
public void testAddCustomerFailure() {
    when(mockRepository.addCustomer(customer))
        .thenThrow(new DataAccessException("DB error"));
    
    assertThrows(DataAccessException.class, () -> {
        customerService.addCustomer(customer);
    });
}
```

---

### 7. Use Descriptive Test Names

```java
// Good
@Test
public void addCustomer_ValidCustomer_ReturnsCustomer()

@Test
public void getAllCustomers_EmptyDatabase_ReturnsEmptyList()

// Bad
@Test
public void test1()

@Test
public void testCustomer()
```

---

### 8. Keep Tests Independent

```java
// Bad - Tests depend on each other
private Customer sharedCustomer;

@Test
public void test1() {
    sharedCustomer = new Customer();
    // Modifies shared state
}

@Test
public void test2() {
    // Depends on test1 running first
    assertNotNull(sharedCustomer);
}

// Good - Each test is independent
@BeforeEach
public void setUp() {
    customer = new Customer();
}
```

---

### 9. Don't Mock What You Don't Own

```java
// Bad - Mocking third-party library classes
@Mock
private HttpClient httpClient;

// Good - Create a wrapper and mock that
public interface HttpClientWrapper {
    Response get(String url);
}

@Mock
private HttpClientWrapper httpClientWrapper;
```

---

### 10. Use @InjectMocks Instead of Manual Injection

```java
// Bad - Manual injection
@Mock
private CustomerRepository repository;

@BeforeEach
public void setUp() {
    customerService = new CustomerService();
    customerService.setCustomerRepository(repository);
}

// Good - Automatic injection
@Mock
private CustomerRepository repository;

@InjectMocks
private CustomerService customerService;
```

---

## Common Mockito Pitfalls

### ❌ Pitfall 1: Forgetting to Stub

```java
@Test
public void testForgotToStub() {
    // Forgot to stub!
    Customer result = customerService.addCustomer(customer);
    
    // result is null because mock returns null by default
    assertNotNull(result); // ❌ Fails!
}

// Fix
@Test
public void testWithStubbing() {
    when(mockRepository.addCustomer(customer)).thenReturn(customer);
    Customer result = customerService.addCustomer(customer);
    assertNotNull(result); // ✓ Passes
}
```

---

### ❌ Pitfall 2: Wrong Argument in Verification

```java
@Test
public void testWrongArgument() {
    Customer customer1 = new Customer();
    customer1.setAccountNo(1001L);
    
    customerService.addCustomer(customer1);
    
    Customer customer2 = new Customer();
    customer2.setAccountNo(1002L);
    
    verify(mockRepository).addCustomer(customer2); // ❌ Wrong customer!
}
```

---

### ❌ Pitfall 3: Stubbing Void Methods Incorrectly

```java
// Bad
when(mockRepository.deleteCustomer(1001L)); // ❌ Won't compile!

// Good
doNothing().when(mockRepository).deleteCustomer(1001L);

// Or for exceptions
doThrow(new RuntimeException()).when(mockRepository).deleteCustomer(1001L);
```

---

### ❌ Pitfall 4: Over-Mocking

```java
// Bad - Mocking too much
@Mock private Customer customer;
@Mock private FullName fullName;
@Mock private String email;  // ❌ Don't mock primitives/Strings!

// Good - Use real objects
Customer customer = new Customer();
FullName fullName = new FullName();
String email = "test@example.com";
```

---

## Running Tests

### Maven Commands

```bash
# Run all tests (including Mockito tests)
mvn test

# Run only CustomerServiceTest
mvn -Dtest=CustomerServiceTest test

# Run specific test method
mvn -Dtest=CustomerServiceTest#addCustomerMockTest test

# Run with coverage
mvn clean test jacoco:report

# Skip tests
mvn install -DskipTests
```

---

### Expected Output

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running sh.surge.kunal.banking.services.CustomerServiceTest

✓ addCustomerMockTest
✓ getAllCustomersMockTest
✓ deleteCustomerMockTest

[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0

[INFO] BUILD SUCCESS
```

---

## Mockito vs Integration Testing

### Unit Test with Mockito

```java
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository repository; // Fake
    
    @InjectMocks
    private CustomerService service; // Real
    
    @Test
    public void test() {
        when(repository.addCustomer(any())).thenReturn(customer);
        // No database involved!
    }
}
```

**Characteristics:**
- ✅ Fast (milliseconds)
- ✅ No external dependencies
- ✅ Isolated
- ❌ Doesn't test integration

---

### Integration Test

```java
@SpringBootTest
public class CustomerServiceIntegrationTest {
    @Autowired
    private CustomerService service; // Real
    
    @Autowired
    private CustomerRepository repository; // Real (with database)
    
    @Test
    public void test() {
        service.addCustomer(customer);
        // Actually hits database!
    }
}
```

**Characteristics:**
- ✅ Tests real integration
- ✅ Catches integration bugs
- ❌ Slow (seconds)
- ❌ Requires database setup

---

### Testing Pyramid

```
        /\
       /  \      E2E Tests (Few, Slow, Expensive)
      /____\
     /      \    Integration Tests (Some, Medium Speed)
    /________\
   /          \  Unit Tests with Mocks (Many, Fast, Cheap)
  /__________\
```

**Recommended Distribution:**
- 70% Unit Tests (Mockito)
- 20% Integration Tests
- 10% End-to-End Tests

---

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Clone and Run

```bash
# Clone the repository
git clone https://github.com/Kunal70616c/java-mockito-unit-testing.git

# Navigate to project directory
cd java-mockito-unit-testing

# Run all tests
mvn test

# Run only Mockito tests
mvn -Dtest=CustomerServiceTest test

# Run with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

---

## Maven Dependencies

The key Mockito dependency added to `pom.xml`:

```xml
<!-- Mockito with JUnit 5 integration -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.21.0</version>
    <scope>test</scope>
</dependency>
```

**What this includes:**
- Mockito Core framework
- JUnit 5 integration (@ExtendWith support)
- All necessary annotations
- Argument matchers
- Verification utilities

---

## Advantages of This Testing Strategy

1. **Fast Execution**: Tests run in milliseconds without database overhead
2. **True Isolation**: Tests only the service layer logic, not repository implementation
3. **Easy Setup**: No database configuration or test data setup required
4. **Reliable**: No flaky tests due to database state or network issues
5. **Comprehensive**: Can easily test edge cases and error scenarios
6. **Maintainable**: Changes to repository implementation don't break service tests
7. **CI/CD Friendly**: Fast tests that can run on every commit
8. **Clear Intent**: Tests clearly show what the service layer should do

---

## Testing Strategy Comparison

### Unit Test with Mockito (This Project)

```java
@Test
public void addCustomerMockTest() {
    when(mockRepository.addCustomer(customer)).thenReturn(customer);
    Customer result = customerService.addCustomer(customer);
    assertEquals(customer, result);
}
```

**Pros:**
- ⚡ Super fast (5-10ms)
- 🎯 Tests only service logic
- 🔧 Easy to set up
- 🧪 Can test all scenarios

**Cons:**
- ❌ Doesn't test database interaction
- ❌ Doesn't catch integration bugs
- ❌ Mock behavior might not match reality

---

### Integration Test (Complementary)

```java
@SpringBootTest
@Test
public void addCustomerIntegrationTest() {
    Customer result = customerService.addCustomer(customer);
    Customer fromDb = customerRepository.getCustomerById(result.getAccountNo());
    assertEquals(customer.getEmail(), fromDb.getEmail());
}
```

**Pros:**
- ✅ Tests real database interaction
- ✅ Catches integration issues
- ✅ Verifies actual SQL queries

**Cons:**
- 🐌 Slow (500ms+)
- 🔧 Complex setup required
- 💾 Requires database

---

## When to Use Mockito vs Real Objects

### Use Mockito For:

#### External Systems
```java
@Mock
private CustomerRepository repository;  // Database

@Mock
private EmailService emailService;      // SMTP server

@Mock
private PaymentGateway paymentGateway; // External API
```

#### Slow Operations
```java
@Mock
private ReportGenerator reportGenerator; // Takes 5 seconds

@Mock
private FileUploadService fileService;   // Network I/O
```

#### Non-Deterministic Behavior
```java
@Mock
private RandomNumberGenerator random;

@Mock
private Clock clock; // Time-based logic
```

#### Testing Error Scenarios
```java
when(mockRepository.addCustomer(any()))
    .thenThrow(new DatabaseException("Connection lost"));
```

---

### Use Real Objects For:

#### Value Objects
```java
// Real objects - simple data containers
Customer customer = new Customer();
FullName fullName = new FullName();
```

#### The Class Under Test
```java
@InjectMocks
private CustomerService customerService; // REAL service being tested
```

#### Simple Utilities
```java
// Real object - no side effects
StringUtils stringUtils = new StringUtils();
```

---

## Advanced Mockito Techniques

### 1. Argument Captors

Capture arguments passed to mocked methods for detailed assertions.

```java
@Captor
private ArgumentCaptor<Customer> customerCaptor;

@Test
public void testArgumentCapture() {
    Customer customer = new Customer();
    customer.setEmail("test@example.com");
    
    customerService.addCustomer(customer);
    
    verify(mockRepository).addCustomer(customerCaptor.capture());
    
    Customer capturedCustomer = customerCaptor.getValue();
    assertEquals("test@example.com", capturedCustomer.getEmail());
}
```

**Use Cases:**
- Verify argument values
- Test object transformations
- Validate complex objects

---

### 2. Spy Objects

Create partial mocks where only some methods are stubbed.

```java
@Spy
private CustomerService customerService;

@Test
public void testWithSpy() {
    // Real method is called unless stubbed
    List<Customer> realResult = customerService.getAllCustomers();
    
    // Stub specific method
    doReturn(mockList).when(customerService).getAllCustomers();
    List<Customer> stubbedResult = customerService.getAllCustomers();
}
```

**Use Cases:**
- Test one method while using real implementation of others
- Verify method calls on the same object
- Gradual testing of legacy code

---

### 3. Multiple Return Values

Simulate different behavior on consecutive calls.

```java
@Test
public void testMultipleReturns() {
    when(mockRepository.getAllCustomers())
        .thenReturn(emptyList())      // First call
        .thenReturn(listWithOneItem)  // Second call
        .thenReturn(listWithTwoItems); // Third call
    
    assertEquals(0, customerService.getAllCustomers().size());  // Empty
    assertEquals(1, customerService.getAllCustomers().size());  // One item
    assertEquals(2, customerService.getAllCustomers().size());  // Two items
}
```

---

### 4. Custom Answers

Implement complex logic for method invocations.

```java
@Test
public void testCustomAnswer() {
    when(mockRepository.addCustomer(any(Customer.class)))
        .thenAnswer(invocation -> {
            Customer arg = invocation.getArgument(0);
            // Custom logic
            arg.setAccountNo(System.currentTimeMillis());
            return arg;
        });
    
    Customer result = customerService.addCustomer(customer);
    assertTrue(result.getAccountNo() > 0);
}
```

---

### 5. Verification with Timeout

Verify asynchronous behavior.

```java
@Test
public void testAsyncOperation() {
    customerService.addCustomerAsync(customer);
    
    // Wait up to 1 second for the call
    verify(mockRepository, timeout(1000)).addCustomer(customer);
}
```

---

### 6. InOrder Verification

Verify method calls happen in specific order.

```java
@Test
public void testCallOrder() {
    customerService.addCustomer(customer);
    customerService.getAllCustomers();
    
    InOrder inOrder = inOrder(mockRepository);
    inOrder.verify(mockRepository).addCustomer(customer);
    inOrder.verify(mockRepository).getAllCustomers();
}
```

---

### 7. Verify No More Interactions

Ensure no unexpected method calls.

```java
@Test
public void testNoExtraInteractions() {
    customerService.addCustomer(customer);
    
    verify(mockRepository).addCustomer(customer);
    verifyNoMoreInteractions(mockRepository);
}
```

---

## Complete Testing Example

Here's a comprehensive test class showing multiple techniques:

```java
@ExtendWith(MockitoExtension.class)
public class CustomerServiceCompleteTest {
    
    @Mock
    private CustomerRepository customerRepository;
    
    @InjectMocks
    private CustomerService customerService;
    
    @Captor
    private ArgumentCaptor<Customer> customerCaptor;
    
    private Customer customer;
    private Faker faker;
    
    @BeforeEach
    public void setUp() {
        faker = new Faker();
        customer = new Customer();
        FullName fullName = new FullName();
        customer.setFullName(fullName);
    }
    
    @Test
    @DisplayName("Add customer - success scenario")
    public void testAddCustomerSuccess() {
        // Arrange
        customer.setAccountNo(1001L);
        customer.setEmail("test@example.com");
        when(customerRepository.addCustomer(customer)).thenReturn(customer);
        
        // Act
        Customer result = customerService.addCustomer(customer);
        
        // Assert
        assertNotNull(result);
        assertEquals(customer.getAccountNo(), result.getAccountNo());
        verify(customerRepository, times(1)).addCustomer(customer);
    }
    
    @Test
    @DisplayName("Add customer - database exception")
    public void testAddCustomerDatabaseException() {
        // Arrange
        when(customerRepository.addCustomer(any()))
            .thenThrow(new DataAccessException("Connection failed"));
        
        // Act & Assert
        assertThrows(DataAccessException.class, () -> {
            customerService.addCustomer(customer);
        });
    }
    
    @Test
    @DisplayName("Get all customers - empty database")
    public void testGetAllCustomersEmpty() {
        // Arrange
        when(customerRepository.getAllCustomers()).thenReturn(List.of());
        
        // Act
        List<Customer> result = customerService.getAllCustomers();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Delete customer - verify interaction")
    public void testDeleteCustomerVerification() {
        // Arrange
        long accountNo = 1001L;
        when(customerRepository.deleteCustomer(accountNo)).thenReturn(true);
        
        // Act
        boolean result = customerService.deleteCustomer(accountNo);
        
        // Assert
        assertTrue(result);
        verify(customerRepository).deleteCustomer(accountNo);
        verifyNoMoreInteractions(customerRepository);
    }
    
    @Test
    @DisplayName("Add customer - capture and verify argument")
    public void testAddCustomerArgumentCapture() {
        // Arrange
        customer.setAccountNo(1001L);
        customer.setEmail("capture@example.com");
        when(customerRepository.addCustomer(any())).thenReturn(customer);
        
        // Act
        customerService.addCustomer(customer);
        
        // Assert
        verify(customerRepository).addCustomer(customerCaptor.capture());
        Customer captured = customerCaptor.getValue();
        assertEquals("capture@example.com", captured.getEmail());
        assertEquals(1001L, captured.getAccountNo());
    }
}
```

---

## Troubleshooting

### Issue 1: UnnecessaryStubbingException

**Error:**
```
org.mockito.exceptions.misusing.UnnecessaryStubbingException:
Unnecessary stubbings detected.
```

**Cause:** Stubbed a method but never called it.

**Solution:**
```java
// Remove unused stubs
when(mockRepository.addCustomer(customer)).thenReturn(customer); // ❌ Never used

// Or use lenient() for optional stubs
lenient().when(mockRepository.addCustomer(customer)).thenReturn(customer);
```

---

### Issue 2: NullPointerException in Test

**Error:**
```
java.lang.NullPointerException
    at CustomerServiceTest.testAddCustomer
```

**Cause:** Forgot to stub the method or mock wasn't initialized.

**Solution:**
```java
// Ensure @ExtendWith is present
@ExtendWith(MockitoExtension.class)

// Ensure method is stubbed
when(mockRepository.addCustomer(customer)).thenReturn(customer);
```

---

### Issue 3: Argument Mismatch in Verification

**Error:**
```
Argument(s) are different! Wanted:
customerRepository.addCustomer(customer1);
Actual invocations have different arguments:
customerRepository.addCustomer(customer2);
```

**Cause:** Verified with wrong argument.

**Solution:**
```java
// Use argument matchers
verify(mockRepository).addCustomer(any(Customer.class));

// Or verify with correct argument
verify(mockRepository).addCustomer(actualCustomerUsed);
```

---

### Issue 4: Cannot Mock Final Class

**Error:**
```
org.mockito.exceptions.base.MockitoException:
Cannot mock/spy class because it is final
```

**Solution:**
```java
// Option 1: Remove final keyword
public class CustomerService { } // Not final

// Option 2: Enable inline mocking (add to src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker)
mock-maker-inline
```

---

## Performance Comparison

### Test Execution Time Comparison

```
Test Type                        | Average Time | Database Required
---------------------------------|--------------|------------------
Unit Test (Mockito)              | 5-15ms      | No
Integration Test (Real DB)       | 200-500ms   | Yes
End-to-End Test                  | 2-5s        | Yes
```

### Example: 100 Test Methods

```
Mockito Tests:        100 × 10ms = 1 second
Integration Tests:    100 × 300ms = 30 seconds
E2E Tests:           100 × 3s = 5 minutes
```

**Conclusion:** Mockito tests are 30-300x faster!

---

## Code Coverage with Mockito

### Service Layer Coverage

With Mockito, you can achieve high coverage of service layer:

```java
public class CustomerService {
    // Method 1
    public Customer addCustomer(Customer customer) {
        return customerRepository.addCustomer(customer);
    }
    
    // Method 2
    public List<Customer> getAllCustomers() {
        return customerRepository.getAllCustomers();
    }
    
    // Method 3
    public boolean deleteCustomer(long accountNo) {
        return customerRepository.deleteCustomer(accountNo);
    }
}
```

**Coverage with 3 Mockito tests:**
- Line Coverage: 100%
- Branch Coverage: 100%
- Method Coverage: 100%

**Without mocking (requires database):**
- Complex setup required
- Slow test execution
- Harder to maintain

---

## Combining JUnit and Mockito

This project demonstrates both approaches:

### CustomerTest (JUnit Only)
```java
@Test
public void testCustomerGettersAndSetters() {
    customer.setAccountNo(1001L);
    assertEquals(1001L, customer.getAccountNo());
}
```
- Tests model objects
- No mocking needed
- Simple assertions

### CustomerServiceTest (JUnit + Mockito)
```java
@Test
public void testAddCustomer() {
    when(mockRepository.addCustomer(customer)).thenReturn(customer);
    Customer result = customerService.addCustomer(customer);
    assertEquals(customer, result);
}
```
- Tests service layer
- Mocks repository
- Verifies integration

---

## Conclusion

This Mockito implementation demonstrates modern Java testing practices:

- **Isolation**: Tests service layer without database dependency
- **Speed**: Fast test execution for rapid feedback
- **Flexibility**: Easy to test edge cases and error scenarios
- **Maintainability**: Changes to repository don't affect service tests
- **Clarity**: Clear separation between unit and integration testing

### Key Takeaways

1. **Mock External Dependencies**: Database, APIs, file systems
2. **Use Real Objects for Value Objects**: Customer, FullName, etc.
3. **Stub Methods You Call**: Define return values with `when().thenReturn()`
4. **Verify Important Interactions**: Use `verify()` for critical calls
5. **Test Both Success and Failure**: Happy path and error scenarios
6. **Keep Tests Fast**: Mockito tests should run in milliseconds
7. **Complement with Integration Tests**: Don't rely solely on mocks

### Testing Strategy Summary

```
Unit Tests (Mockito)          Integration Tests        E2E Tests
├── Fast (5-15ms)            ├── Medium (200-500ms)   ├── Slow (2-5s)
├── No database              ├── Real database        ├── Full stack
├── Isolated                 ├── Integration          ├── User scenarios
├── Many tests (70%)         ├── Some tests (20%)     └── Few tests (10%)
└── Run on every commit      └── Run before merge     └── Run before release
```

---

## Additional Resources

- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Mockito GitHub](https://github.com/mockito/mockito)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Base JDBC Application](https://github.com/Kunal70616c/spring-jdbc-data-access.git)
- [JUnit Testing Project](https://github.com/Kunal70616c/jdbc-junit-unit-testing.git)

---

## License

This project is open-source and available for educational purposes.

## Author

Kunal - [GitHub Profile](https://github.com/Kunal70616c)
