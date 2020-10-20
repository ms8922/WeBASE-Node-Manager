package com.webank.webase.node.mgr;

import com.webank.webase.node.mgr.deploy.service.PathService;

public class main {

    public static void main(String args[]){

        String chainRootOnHost="/data_deepin/test/kubetest/default_chain";

        String nodeRootOnHost = PathService.getNodeRootOnHost(chainRootOnHost, 0);
        String yml = String.format("%s/application.yml", nodeRootOnHost);
        String sdk = String.format("%s/sdk", chainRootOnHost);
        String front_log = String.format("%s/front-log", nodeRootOnHost);

        String dockerCreateCommand = String.format("sudo docker run -d --rm --name %s " +
                "-v %s:/data " +
                "-v %s:/front/conf/application-docker.yml " +
                "-v %s:/data/sdk " +
                "-v %s:/front/log " +
                "-e SPRING_PROFILES_ACTIVE=docker " +
                "--network=host -w=/data %s ", "test" , nodeRootOnHost, yml,sdk,front_log, "fiscoorg/fisco-webase:v2.5.0");


        System.out.println(dockerCreateCommand);

    }


}
