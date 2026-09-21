# Friday session deliverables

## This Friday will end with demonstrations (no presentations)

### Goals    
- Design your API contract as a team before anyone writes a controller: the operations, the shape of a response, and what an error looks like, consistently, across every one of them.    
- Trace one order end to end through your layers and check whether a business decision has leaked into the wrong one.    
- Decide as a team whether you're leaving this service open for now, stubbing authentication, or building it for real, and record the decision rather than letting it happen by default.    
- Walk through error handling: what does the API actually return for each failure case your Sprint 5 domain already defines?   
- Get the service running as a container in your own environment, alongside what you've already built.
- Revisit the OWASP Top 10 material from Sprint 2 against this specific service, not as a checklist exercise, what actually applies here?
- Refine the backlog against what building the API has taught you about the schema and the rules underneath it.

### What to demonstrate
- OpenAPI yaml file for the API
- HTML page that describes the API (generated from your yaml file)
- Demonstrate an order flowing through the layers
- Discuss your decision on authentication
- Demonstrate a second containerized version of the application.