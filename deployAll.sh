sources=( "eureka-service" "sample-service" "sample-client" "sample-dashboard" )

for src in "${sources[@]}"
do
   echo "$src is being deployed"

   cd ${src}
   source deploy.sh
   cd ..
done
