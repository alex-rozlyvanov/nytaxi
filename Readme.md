# NYTAXI

## This is the NYTAXI - a microservice, event-driven application.

### Main technologies:

- Gradle 7.5.1
- Java 17
- Postgres 14
- Spring Boot 2.7.5
- Flyway 8.5+
- Lombok 1.18+
- Slf4j

---

- Junit 5 + Mockito + Assertj
- TestContainers 1.17

### Responsibilities:

#### [FRONT](./front):

- Web service exposing reactive JSON REST API process taxi events and push it to the Kafka message queue.
- When totals are requested the service will read them directly from Totals Cache (Redis) and return them to the client.

#### [BACK](./back):

- Kafka consumers for taxi messages, transforms them, and inserts them into the PostgreSQL database.

#### [CALCULATOR](./calculator):

- Is a scheduled job running at intervals to calculate the monthly and daily totals and cache them in the Totals Cache.

#### [CLIENT](./calculator):

- Application load testing client.
- Sending taxi events to the server-side via POSTs and retrieving
  calculated totals via GETs

#### [COMMON](./calculator):

- Module for common DTOs, utilities

## How to build:

**Regular build:** `./gradlew clean build`

**Without tests build:** `./gradlew clean build -x test -x integrationTest`

**Run local in docker
- Install [Kubernetes](https://kubernetes.io/docs/setup/)
- Install [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- Install [Helm](https://helm.sh/docs/intro/install/)
- Enable ingress addon `minikube addons enable ingress`
- Point the local Docker daemon to the minikube internal Docker registry - `minikube docker-env && eval $(minikube -p minikube docker-env)`
- Build the app `./gradlew clean build`
- Build the image `docker build . -t nytaxi:local` \
*to update the app build new version or delete rebuild image in minikube registry
- Configure kubectl context `kubectl config set-context --cluster=minikube --namespace=local-dev --user=minikube local-dev`
- Set kubectl context `kubectl config use-context local-dev`
- Update local /etc/hosts with `{{app local host}} {{minikube ip}}`
- Install app with helm 
```shell
  helm upgrade local-dev config/cluster/helm/application \
  --install --create-namespace --kube-context=local-dev \
  --set image.tag=local \
  --set image.repository=nytaxi \
  -f ./config/cluster/helm/application/values-local.yaml
```
** Local clean up
- `helm uninstall local-dev --kube-context=local-dev`
- `kubectl delete namespace local-dev`


**Push docker image to ECR:**

- aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin
  {{registry}}
- docker build -t nytaxi:0.0.1 .
- docker tag nytaxi:0.0.1 {{registry}}/nytaxi:0.0.1
- docker push {{registry}}/nytaxi:0.0.1

========================================================================================= \
========================================================================================= \
https://medium.com/swlh/how-to-run-locally-built-docker-images-in-kubernetes-b28fbc32cc1d
https://kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/

```shell
# INSTALL
kubectl config set-context --cluster=minikube --namespace=local-dev --user=minikube local-dev
helm upgrade local-dev config/cluster/helm/application \
--install --create-namespace --kube-context=local-dev 
-f ./config/cluster/helm/application/values-local.yaml

# TRIGGER client job
kubectl patch job/client --type=strategic --patch '{"spec":{"suspend":true}}'

# CLEAN UP
helm uninstall local-dev --kube-context=local-dev
kubectl delete namespace local-dev
```


# One time actions
`eksctl create cluster -f config/cluster/eks.yml`
`aws eks update-kubeconfig --region eu-central-1 --name nytaxi-cluster --alias dev`
`kubectl config set-context --current=true --namespace=dev`


# How to update addons
[//]: # (`helm repo add aws-ebs-csi-driver https://kubernetes-sigs.github.io/aws-ebs-csi-driver && helm pull aws-ebs-csi-driver/aws-ebs-csi-driver --untar`)
`helm repo add eks https://aws.github.io/eks-charts && helm pull eks/aws-load-balancer-controller --untar`
`helm repo add secrets-store-csi-driver https://kubernetes-sigs.github.io/secrets-store-csi-driver/charts && helm pull secrets-store-csi-driver/secrets-store-csi-driver --untar`
`helm repo add aws-secrets-manager https://aws.github.io/secrets-store-csi-driver-provider-aws && helm pull aws-secrets-manager/secrets-store-csi-driver-provider-aws --untar`
