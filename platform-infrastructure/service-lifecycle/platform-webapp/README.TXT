Attention!!!

To successfully build, deploy and test this WAB (web application bundle),
follow the few lines of guidelines as below.

1. This project uses example Math service to demonstrate the 
	consumption of OSGI service within Web app bundle
	
	So, Compile the following two project and deploy to pickup first
	
	1- SOCIETIES-Platform\example\example.service.api
	2- SOCIETIES-Platform\example\example.service.provider
	
2. Since, this WAB does not include any 3rd party library(dependency) jars into
	/META_INF/lib/. you need to drop the jars (which are not provided 
	by diployment container -virgo) so copy and drop following jars to
	Virgo_home/repository/usr folder
	
	1. org.springframework.osgi.web-1.2.1.jar
	2. com.springsource.javax.validation-1.0.0.GA.jar
	
	these dependency jars are already configured in maven pom file, 
	hence it will be downloaded to your local maven repository while maven build
	
3. Build the project as normal using mvn clean install and deploy the war file to 
	pickup directory
	
	goto web browser and type http://localhost:8080/societies/login.html to start the web app
	 
	