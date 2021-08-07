#bin/bash

curl -X POST localhost:8082/alter -d '{"drop_op": "DATA"}'
