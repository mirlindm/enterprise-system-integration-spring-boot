#/bin/bash
echo Please enter the docker image name:
read img_name
echo Please enter the heroku app name
read app_name

docker rmi $img_name --force
docker rmi registry.heroku.com/$app_name/web --force
maven clean
mvn install -DskipTests
yes | cp target/*.war docker/
cd docker
docker build -t $img_name:latest .
docker tag $img_name:latest registry.heroku.com/$app_name/web
docker push registry.heroku.com/$app_name/web
heroku container:release web --app $app_name
heroku logs --tail --app $app_name
