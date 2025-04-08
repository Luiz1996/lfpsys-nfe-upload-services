This is the NFe Upload Control Service of LFPSys.

#### Main Technology Stack
* Java 21
* PostgreSQL
* Apache Kafka
* Redis
* Maven 3.9.1 (or higher)
* [Spring Boot Framework](https://spring.io/projects/spring-boot)

#### Communication Contracts
##### REST
	*** NFe Upload ***
	
	POST https://lfpsys.com/lfpsys-nfe-upload-services/upload
	
	Headers:
	
		{
			"Authorization": "token"
		}
	
	Request Body:
	
		multipart/form-data
	
		Key: File
		Type: File
		File: C://my-nfe.xml

##### KAFKA
	Tópico: nfe-upload
	Headers:
		client_id: <UUID>,
		nfe_key: <String>
	Payload:
		{
			"xml": <String>
		}
		
	Tópico: products
	Headers:
		client_id: <UUID>
	Payload:
		{
			"products": [
				{
					"name": "Mouse Gamer",
					"value": "300.00"
				},
				{
					"name": "Mousepad Gamer",
					"value": "100.00"
				}
			]
		}