## 修复验证码的问题

docker exec -u 0 -it $(docker ps -q -f name=rurals_gateway-service | head -1) bash <p>
apt-get update && apt-get install --assume-yes fontconfig