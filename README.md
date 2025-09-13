

### Example of Scala-based microservice application on a Kubernetes (minikube) cluster
## Stack: Scala 2.13, http4s, fs2-kafka, cats-effects, doobie, Docker + Kubernetes

__web-srv__     retrieves data from in-cluster DB with Doobie and fans out to clients with Kafka   
__web-client__  provides oauth authorization based on github.com and accepts messages from the server 
__web-common__  contains custom cache implementation and common data models  