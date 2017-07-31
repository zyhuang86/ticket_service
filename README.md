Ticket Service
==============

Ticket service provides a set of API endpoints to hold and reserve seats at a mocked venue. However, it doesn't
run against a database but rather some pre-loaded seating data. In order to reserve seats, one must place them on
hold first before proceeding to reserve the seats with the hold id. All seats on hold are configured to expire in
30 seconds. If the seats are not reserved within that time interval, the seats will free up.

Seating assignment is determined programmatically by the service. Ticket service prioritizes keeping seating requests
as together as possible. As long as there are room at any row to keep each seating request in tact, ticket service
will place those seats on hold rather than always filling the seats closest to the stage. If unable to fit everyone
in a request together, ticker service will attempt to fragment the group as little as possible by assigning largest
set of neighboring seats to the group before moving down the list.

API
---
The following endpoints are available for interaction with ticker service

    /ticketService/availableSeatCount
        - Returns an integer

    /ticketService/findAndHoldSeats?numberOfSeats={numberOfSeatsToHold}&email={E-mail address}
        - Returns seatHoldId along with information on the seats placed on hold

    /ticketService/findAndReserveSeats?seatHoldId={seatHoldId}&email={E-mail address}
        - Reserves the seats after placing them on hold

Build & Run
---
To compile and build, run the following command at the root folder

    mvn clean install

The build will run both unit tests and integration tests by default, run the following to skip the test runs

    mvn clean install -Dmaven.test.skip=true

To run the application, provide it with the following program argument

    server configuration.yml

