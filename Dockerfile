# build command :
#   cat Dockerfile-Node-Manager | docker build -t yuanmomo/webase-node-manager:1.3.2 -f - https://github.com/WeBankFinTech/WeBASE-Node-Manager.git#dev131
#
# run command :
#   docker run --rm --name "manager" --network=host yuanmomo/webase-node-manager:1.3.2
#

FROM openjdk:8-jdk-alpine as prod

ENV CLASSPATH "/dist/conf/:/dist/apps/*:/dist/lib/*"
ENV JAVA_OPTS " -server -Dfile.encoding=UTF-8 -Xmx256m -Xms256m -Xmn128m -Xss512k -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/log/heap_error.log  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 "
ENV APP_MAIN "com.webank.webase.node.mgr.Application"
ENV LANG en_US.UTF-8

RUN apk --no-cache add --update ttf-dejavu fontconfig bash curl wget

COPY ./dist/lib                  /dist/lib
COPY ./dist/conf_template        /dist/conf
COPY ./dist/apps                 /dist/apps

WORKDIR /dist
EXPOSE 5001

# start commond
ENTRYPOINT java ${JAVA_OPTS} -Djdk.tls.namedGroups="secp256k1", -Duser.timezone="Asia/Shanghai" -Djava.security.egd=file:/dev/./urandom, -Djava.library.path=/dist/conf -cp ${CLASSPATH}  ${APP_MAIN}
