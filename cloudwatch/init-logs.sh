#!/bin/bash
echo "Inicializando infraestructura de CloudWatch Logs..."

# Creación de grupo y stream de productos
awslocal logs create-log-group --log-group-name /productos/logs
awslocal logs create-log-stream --log-group-name /productos/logs --log-stream-name productos-local

# Creación de grupo y stream de órdenes
awslocal logs create-log-group --log-group-name /ordenes/logs
awslocal logs create-log-stream --log-group-name /ordenes/logs --log-stream-name ordenes-local

# Creación de grupo y stream de pagos
awslocal logs create-log-group --log-group-name /pagos/logs
awslocal logs create-log-stream --log-group-name /pagos/logs --log-stream-name pagos-local

echo "¡Grupos y Streams de CloudWatch creados exitosamente!"