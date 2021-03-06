.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2018 Huawei Technologies Co., Ltd.

.. Step to import VNF/PNF:


Step to import VNF/PNF
==============================================

This document describes how to insert existing PNF/VNF to AAI with CLI project.

Main steps:
1. create customer and service instance in AAI. This step could be done by UUI or VID, OR user can insert customer/service instance node by CLI cmd.

2. create PNF/VNF

3. create relation-ship between service-instance and created PNF/VNFs

4. check the Topology graph through AAI portal


create customer
================
optional,since the customer/subscription/service-instance may already exist

create customer:
--------------------

::

  onap>customer-create -u AAI -p AAI -m https://172.19.44.123:8443 --customer-name testCustomer --subscriber-name EC

Check if customer created successfully:
-------------------------------------------

::

  onap>customer-list -u AAI -p AAI -m https://172.19.44.123:8443

  +--------------+------------------+
  |name          |resource-version  |
  +--------------+------------------+
  |testCustomer  |1521772326346     |
  +--------------+------------------+
  |Orange1       |1521771120855     |
  +--------------+------------------+
  |Orange        |1520304126184     |
  +--------------+------------------+
  |test          |1521098144163     |
  +--------------+------------------+

delete customer cmd:
-------------------------------------------

::

  onap>customer-delete -u AAI -p AAI -m https://172.19.44.123:8443 --customer-name testCustomer --resource-version 1521772326346


create subscription (optional)
==============================

create subscription cmd:
-------------------------

::


  onap>subscription-create-with-template -u AAI -p AAI -m https://172.19.44.123:8443 --customer-name testCustomer --service-type EC --template /opt/oclip/template/sub-create.json

        content of /opt/oclip/template/sub-create.json:
            {
            "service-subscription": [
                {
                    "service-type": "EC",
                }
                 ]
                }



Check if subscription created successfully:
--------------------------------------------

::

  onap>subscription-list -u AAI -p AAI -m https://172.19.44.123:8443 --customer-name testCustomer

  output:
  +--------------+------------------+
  |service-type  |resource-version  |
  +--------------+------------------+
  |EEC           |1521773231094     |
  +--------------+------------------+
  |EC            |1522058350020     |
  +--------------+------------------+

delete subscription cmd:
--------------------------------------------

::

  onap>subscription-delete -u AAI -p AAI -m https://172.19.44.123:8443 --customer-name testCustomer --service-type EC --resource-version 1521772326346

create service instance(optional)
=================================

create service instance with template cmd:
------------------------------------------


::

  onap>service-instance-create-with-template -u AAI -p AAI -m https://172.19.44.123:8443 --service-instance-id 176d9eba-1662-4289-8396-0097b50fd486 --template /opt/oclip/open-cli-schema/service-instance-template.json  --global-customer-id testCustomer --service-type EC

   content of /opt/oclip/open-cli-schema/service-instance-template.json:

        {
            "global-customer-id": "testCustomer",
            "subscriber-name": "EC",
            "subscriber-type": "INFRA",
            "service-subscriptions": {
                "service-subscription": [
                    {
                        "service-type": "EC",
                        "service-instances": {
                            "service-instance": [
                                {
                                    "service-instance-id": "176d9eba-1662-4289-8396-0097b50fd486",
                                    "service-instance-name": "template-service",
                                    "service-type": "NetworkService",
                                    "relationship-list": {
                                    }
                                }
                            ]
                        }
                    }
                ]
            }
        }


Create PNF
==========

    there are many ways to create PNF, cmd:

A: create pnf with pnf name:
----------------------------

::

  onap>pnf-create -u AAI -p AAI -m https://172.19.44.123:8443 -n testcmdpnfname

B: create pnf with all option (including relationship json):
------------------------------------------------------------


::

  onap>pnf-create -u AAI -p AAI -m https://172.19.44.123:8443 -n testcmdpnfname -q MME -x generic --in-maint false --prov-status PROV --relationship /opt/oclip/open-cli-schema/pnf-sub-relation.json

       Sample content of /opt/oclip/open-cli-schema/pnf-sub-relation.json:

           {
                "relationship": [
                    {
                        "related-to": "logical-link",
                        "related-link": "/aai/v11/network/logical-links/logical-link/S11-00001",
                        "relationship-data": [
                            {
                                "relationship-key": "logical-link.link-name",
                                "relationship-value": "S11-00001"
                            }
                        ]
                    }
                  ]
        }

C: create pnf with template
----------------------------
since pnf contains logs of parameters , user can put all the parameters in a json file.e.g to create a PNF with p-interface,user should use this cmd:


::

  onap>pnf-create-with-template -u AAI -p AAI -m https://172.19.44.123:8443 -n pnf_template -r /opt/oclip/open-cli-schema/pnf-template.json

        Sample content of /opt/oclip/open-cli-schema/pnf-template.json

        {
            "pnf-name" : "pnf_template",
            "equip-type" : "pnf_template",
            "equip-vendor" : "Generic",
            "in-maint" : "false",
            "prov-status" : "PROV",
            "p-interfaces" : {
                 "p-interface" : [
                     {
                        "interface-name" : "pnf_template-p-interface",
                        "speed-value" : "1",
                        "speed-units" : "Gbps",
                        "port-description" : "downstream  port 1",
                        "interface-type" : "port",
                        "prov-status" : "PROV",
                        "in-maint" : "false",
                        "l-interfaces" : {
                              "l-interface" : [
                                   {
                                        "interface-name" : "pnf_template-i-interface",
                                        "interface-role" : "Eth logical interface",
                                        "is-port-mirrored" : "false",
                                        "prov-status" : "PROV",
                                        "in-maint" : "false"
                                   }
                               ]
                        }
                    }
                 ]
              }
            }

List Created PNF cmd:
-----------------------


::

  onap>pnf-list -u AAI -p AAI -m https://172.19.44.123:8443
    output:
            +----------------+--------------------------------------+------------------+
            |pnf-name        |pnf-id                                |resource-version  |
            +----------------+--------------------------------------+------------------+
            |batch-name-2    |176d9eba-1662-4289-8396-0097b50fd470  |1521790894608     |
            +----------------+--------------------------------------+------------------+
            |pnf_template    |176d9eba-1662-4289-8396-0097b50fd467  |1521702068121     |
            +----------------+--------------------------------------+------------------+
            |testcmdpnfname  |176d9eba-1662-4289-8396-0097b50fd466  |1521687589914     |
            +----------------+--------------------------------------+------------------+
            |batch-name-1    |176d9eba-1662-4289-8396-0097b50fd470  |1521790894391     |
            +----------------+--------------------------------------+------------------+
            |SPGW-0001       |                                      |1520304310122     |
            +----------------+--------------------------------------+------------------+
            |test            |                                      |1520417818047     |
            +----------------+--------------------------------------+------------------+
            |MME-000111      |                                      |1520417147010     |
            +----------------+--------------------------------------+------------------+
            |MME-0001        |                                      |1520303982165     |
            +----------------+--------------------------------------+------------------+
            |SP GW-0001      |                                      |1520304000840     |
            +----------------+--------------------------------------+------------------+

Delete PNF cmd:
----------------

::

  onap>pnf-delete -n testname -b 1521685031379 -u AAI -p AAI -m https://172.19.44.123:8443

Create VNF
===========

    there are many ways to create VNF, cmd:

A: create VNF with VNF id:
---------------------------

::
  onap>vnf-create -u AAI -p AAI -m https://172.19.44.123:8443  --name vn1 --vnf-id d9b1b05f-44c8-45ef-89aa-d27ad060ceb8 --vnf-type t1 --debug

B: create VNF with template:
-----------------------------

::

  onap>vnf-create-with-template -u AAI -p AAI -m https://172.19.44.123:8443 --vnf-id d9b1b05f-44c8-45ef-89aa-d27ad060ceb9 --template /opt/oclip/open-cli-schema/vnf-template.json

        Sample content of /opt/oclip/open-cli-schema/vnf-template.json
        {
            "vnf-id": "d9b1b05f-44c8-45ef-89aa-d27ad060ceb9",
            "vnf-name": "vvnf-name",
            "vnf-type": "vnf-type-1",
            "in-maint": true,
            "is-closed-loop-disabled": false
        }

Create relationship between service instance and PNF/VNF:
=========================================================

::

  onap>service-instance-relationship-create -u AAI -p AAI -m https://172.19.44.123:8443 -g Orange -z EC -i 176d9eba-1662-4289-8396-0097b50fd485 -r /opt/oclip/open-cli-schema/relation.json

    Sample content of  /opt/oclip/open-cli-schema/relation.json:

            {
                        "related-to": "pnf",
                        "related-link": "/aai/v11/network/pnfs/pnf/pnf_template",
                        "relationship-data": [
                            {
                                "relationship-key": "pnf.pnf-name",
                                "relationship-value": "pnf_template"
                            }
                        ]
            }

List Service-instance relationship:
------------------------------------

::

  onap>service-instance-relationship-list -u AAI -p AAI -m https://172.19.44.123:8443 -g Orange -z EPC -i 176d9eba-1662-4289-8396-0097b50fd485

    Output:

        +--------------+----------------------------------------------------+
        |related-to    |related-link                                        |
        +--------------+----------------------------------------------------+
        |pnf           |/aai/v11/network/pnfs/pnf/pnf_template              |
        +--------------+----------------------------------------------------+
        |pnf           |/aai/v11/network/pnfs/pnf/testcmdpnfname            |
        +--------------+----------------------------------------------------+
        |logical-link  |/aai/v11/network/logical-links/logical-link/S11-00  |
        |              |001                                                 |
        +--------------+----------------------------------------------------+
        |pnf           |/aai/v11/network/pnfs/pnf/MME-0001                  |
        +--------------+----------------------------------------------------+
        |pnf           |/aai/v11/network/pnfs/pnf/SP%20GW-0001              |
        +--------------+----------------------------------------------------+

Delete Service-instance relationship:
--------------------------------------

::

  onap>service-instance-relationship-delete -u AAI -p AAI -m https://172.19.44.123:8443 -g Orange -z EPC -i 176d9eba-1662-4289-8396-0097b50fd485 -r /opt/oclip/open-cli-schema/relation.json

    Sample content of     /opt/oclip/open-cli-schema/relation.json is same as the one used to create relation.

batch import PNF/VNF:
=====================
    Since all the cmd support batch model, user can import multi-PNF/VNF one time:
    This CMD should be run on system terminal:

cmd:
-------

::

  onap>oclip -p create-batch.yaml pnf-create

        Sample content of create-batch.yaml:

        pnf1:
          - name: batch-name-1
          - host-username: AAI
          - host-password: AAI
          - host-url: https://172.19.44.123:8443

        pnf2:
          - name: batch-name-2
          - host-username: AAI
          - host-password: AAI
          - host-url: https://172.19.44.123:8443
          - template: |
                        {
                            "relationship": [
                                {
                                    "related-to": "logical-link",
                                    "related-link": "/aai/v11/network/logical-links/logical-link/S11-00001",
                                    "relationship-data": [
                                        {
                                            "relationship-key": "logical-link.link-name",
                                            "relationship-value": "S11-00001"
                                        }
                                    ]
                                }
                            ]
                         }

User can also use create with template cmd for batch execute:

sample cmd:
----------------

::

  oclip -p create-batch.yaml pnf-create-with-template

Checke AAI topology through portal:
====================================

Typing the key word, (service,PNF,generic-vnf,customer),the search text box will pup up auto suggestion of the search key word.
e.g.
::

    service-instance called 176d9eba-1662-4289-8396-0097b50fd485
    customer called test
    pnf called MME-0001
    generic-vnf called d9b1b05f-44c8-45ef-89aa-d27ad060ceb4






