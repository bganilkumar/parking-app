This application is created using Spring Boot 3 + JDK 17 and is developed as a REST API.
Number of parking spaces and price can be configured in application.properties

<b> How to run the application </b>
<li> Setup the application with JDK 17 and maven 3. </li>
<li> Run the ParkingApplication.java from your IDE. </li>
<li> Or run via <b> mvn spring-boot:run </b> </li>
<li> After application starts, refer to the ParkingController.java file for available API's.</li>

<b> API flow </b>
<li>/parking/park</li>
<li>/paring/exit</li>
<li>to list all vehicles -> /parking/admin/vehicles</li>

<b> Things Assumed </b>

<li> Timezone is UTC. </li>
<li> All access is authorized. </li>

<b> Future Enhancements </b>

<li> Duplicate Vehicle Registration Numbers identifications.</li>
<li> Price Scope (Mins/Hrs/Days). </li>
<li> Data persistence. </li>
<li> Authorization. </li>
<li> Vehicle based pricing. </li>
