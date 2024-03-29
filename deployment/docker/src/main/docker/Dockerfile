# Copyright 2018 Huawei Technologies Co., Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM openjdk:11.0.6-jre-slim

RUN apt-get update && apt-get install -y sudo

RUN groupadd -r ocomp && useradd -m --no-log-init -r -g ocomp ocomp && \
    usermod -aG sudo ocomp && echo "ocomp ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers && \
    chmod -R 777 /usr/local/

USER ocomp

ENV OPEN_CLI_HOME=/opt/oclip \
    OPEN_CLI_DEBUG=false \
    OPEN_CLI_DEBUG_PORT=5005 \
    OPEN_CLI_MODE=shell \
    OPEN_CLI_PRODUCT_IN_USE=onap-dublin \
    GOTTY_TITLE_FORMAT="{{ .command }}"

ADD ./STAGE $OPEN_CLI_HOME
WORKDIR $OPEN_CLI_HOME

RUN sudo apt-get install -y lighttpd git wget pandoc vim && \
    cd /tmp && wget https://github.com/yudai/gotty/releases/download/v1.0.0/gotty_linux_amd64.tar.gz && \
    tar -xvf gotty_linux_amd64.tar.gz && chmod +x ./gotty && \
    mv ./gotty /usr/local/bin/ && \
    sudo chown -R ocomp:ocomp $OPEN_CLI_HOME && pandoc -t plain $OPEN_CLI_HOME/docs/README.md > $OPEN_CLI_HOME/docs/oclip-readme.txt && \
    sudo apt-get purge -y pandoc && sudo apt-get autoremove -y && sudo apt-get clean && sudo rm -rf /var/lib/apt/lists/* /tmp/go /tmp/gotty /tmp/* /var/tmp/* && \
    chmod +x  $OPEN_CLI_HOME/bin/oclip.sh  && \
    chmod +x  $OPEN_CLI_HOME/bin/oclip-rcli.sh  && \
    chmod +x  $OPEN_CLI_HOME/bin/oclip-grpc-server.sh  && \
    ln  $OPEN_CLI_HOME/bin/oclip.sh /usr/local/bin/oclip  && \
    ln  $OPEN_CLI_HOME/bin/oclip.sh /usr/local/bin/onap  && \
    ln  $OPEN_CLI_HOME/bin/oclip-grpc-server.sh /usr/local/bin/oclip-grpc  && \
    if [ ! -d  $OPEN_CLI_HOME/data ]; then mkdir  $OPEN_CLI_HOME/data; fi  && \
    if [ ! -d  $OPEN_CLI_HOME/open-cli-schema ]; then mkdir  $OPEN_CLI_HOME/open-cli-schema; fi && \
    if [ ! -d  $OPEN_CLI_HOME/open-cli-sample ]; then mkdir  $OPEN_CLI_HOME/open-cli-sample; fi && \
    sudo touch /var/log/lighttpd/access.log && \
    sudo cp /etc/lighttpd/conf-available/10-accesslog.conf /etc/lighttpd/conf-enabled/ && \
    sudo cp $OPEN_CLI_HOME/http/lighttpd/lighttpd.conf /etc/lighttpd/lighttpd.conf && \
    sudo mkdir -p /var/www-data/servers/open-cli/  && \
    sudo cp $OPEN_CLI_HOME/http/web/*.* /var/www-data/servers/open-cli/ && \
    sudo cp $OPEN_CLI_HOME/http/lighttpd/10-proxy.conf /etc/lighttpd/conf-enabled/ && \
    sudo cp $OPEN_CLI_HOME/installer/cli-*.zip /var/www-data/servers/open-cli/oclip.zip && \
    cp $OPEN_CLI_HOME/http/web/ocomp.crt ~/.gotty.crt && \
    cp $OPEN_CLI_HOME/http/web/ocomp.key ~/.gotty.key && \
    sudo cp $OPEN_CLI_HOME/http/web/ocomp.pem /etc/lighttpd/ocomp.pem
    #openssl req -x509 -nodes -days 9999 -newkey rsa:2048 -keyout ~/.ocomp.key -out ~/.ocomp.crt -subj "/C=IN/ST=KA/L=BLR/O=CLI/CN=onap"
EXPOSE 443
EXPOSE 9443
EXPOSE 50051
ENTRYPOINT if [ "$OPEN_CLI_MODE" = "daemon" ]; then sudo service lighttpd start; gotty -t --port 9443 --permit-write --reconnect oclip; \
           elif [ "$OPEN_CLI_MODE" = "ocs-web" ]; then echo "****** OCLIP Web Command Server ******"; sudo service lighttpd start; \
           elif [ "$OPEN_CLI_MODE" = "ocs-grpc" ]; then echo "****** OCLIP gRPC Command Server ******"; oclip-grpc; \
           elif [ "$OPEN_CLI_MODE" = "occ" ]; then echo "****** OCLIP Web Command Console (OCC) ******"; gotty -t  --port 9443 --permit-write --reconnect oclip; \
           else echo "****** OCLIP Command Shell******"; oclip -v && /bin/bash; fi
