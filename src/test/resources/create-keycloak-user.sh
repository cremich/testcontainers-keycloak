#!/usr/bin/env bash
cd keycloak/bin
./kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user admin --password admin

USERID=$(./kcadm.sh create users -r demo -s username=monitoring-user -s enabled=true -o --fields id | jq '.id' | tr -d '"')
echo $USERID
./kcadm.sh update users/$USERID/reset-password -r demo -s type=password -s value=default -s temporary=false -n
./kcadm.sh add-roles --uusername monitoring-user --rolename monitoring -r demo
