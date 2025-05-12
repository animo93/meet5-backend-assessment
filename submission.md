#### Requirements
1. Create a performant database structure for a rela�onal database to store the
   following data: User profile with common data like name, age, all profile visits they
   do, all the user profiles they like (Do NOT use hibernate/JPA)
2. Create a query that retrieves all profile visitors of a user, sorted by the way you think
   best
3. Design a Java class that models user profiles with various attributes, including name,
   age, and additional user-defined fields.
4. Implement validation mechanisms to ensure data consistency and integrity within
   the class.
5. Develop a /user/visit API which will be used to record a when user A visits user B.
6. Develop a /user/like API which will be used to record when user A likes user B.
7. If a user has visited and liked 100 users within the first 10 minutes, then mark that
   user as “fraud”.
8. Develop a data insertion method that handles bulk data insertion without using
   Hibernate or JPA.
9. Analyze the existing monolithic backend's components, interactions, and
   dependencies.Propose a detailed microservices architecture, including services, APIs,
   data flow, and communication mechanisms. Address challenges such as data
   consistency, API versioning, and fault tolerance in your proposal.

#### Assumptions
1. All the API calls are authenticated and authorized.
2. Any user can visit & like any other user.
3. A user can only visit and like a user once.
4. A user visiting and liking 100 users within 10 minutes after being created will be
   marked as “fraud”.
5. Currently, it's unclear that Marking a user as “fraud” will prevent them from 
   visiting and liking other users or not.

#### Proposed Solution
1. Pre-populate the database with 1000 users during startup each having a unique user_id but same password 
   called "password" with "NEW" status.
2. Use a relational database Postgres to store user profiles, visits, and likes.
3. Expose 4 APIs using basic authentication:
   - (POST) v1/users/: to record when user A likes user B.
   - (PUT) v1/users/<user-id-B>/visit: to record when user A visits user B.
   - (PUT) v1/users/<user-id-B>/like: to record when user A likes user B.
   - (GET) v1/users/<user-id-A/visitors: to retrieve all profile visitors of user A.
4. Store each and every visit in the "visits" table in an append-only manner 
   (i.e., no updates or deletes).
5. Store each and every like in the "likes" table in an append-only manner 
   (i.e., no updates or deletes).
6. Use an async job to check all user profiles with a "NEW" status and check if he/she
   has visited and liked 100 users within the initial 10 minutes of creation and mark them as "Fraud" or "Valid"

#### High Level Design
1. Database Design
   - User Table
     - user_id (Primary Key)
     - name
     - age
     - created_at
     - status
       - NEW
       - VALID
       - FRAUD
   - Visit Table
     - visitor_id (Composite key) (Foreign Key referencing User)
     - visited_id (Composite key) (Foreign Key referencing User)
     - created_at
   - Like Table
     - liker_id (Composite key) (Foreign Key referencing User)
     - liked_id (Composite key) (Foreign Key referencing User)
     - created_at
2. API Design
    - POST v1/user/: to record when user A likes user B.
      - Request Body: { "name": "Test1234", "age": "23", "password": "password" }
      - Response: 200 OK
    - PUT v1/user/<user-id-B>/visit: to record when user A visits user B.
      - Request Body: { "user_id": "user_id_A" }
      - Response: 200 OK
    - PUT v1/user/<user-id-B>/like: to record when user A likes user B.
      - Request Body: { "user_id": "user_id_A" }
      - Response: 200 OK
    - GET v1/user/<user-id-A/visitors: to retrieve all profile visitors of user A.
      - Response: 200 OK
      - Response Body: { "visitors": [ { "user_id": "1234", "name": "Test1234", "age": "23", "status": "Fraud" } ] }