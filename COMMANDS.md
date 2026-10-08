1	docker compose up -d
	 Starts all containers defined in docker-compose.yml (currently just Postgres) in the background (-d = detached, so your terminal stays free). Runthis once per work session, from the folder containing docker-compose.yml. Safe to run again even if it's already running — it won't create a duplicate.
2	docker ps
	 Lists all currently RUNNING containers — name, status, ports. Use this any time to confirm shopsphere-postgres is actually up before starting your Spring Boot app. If it's missing, it's not running.
3	docker ps -a
	 Like docker ps, but also shows STOPPED/exited containers. Use this when a container seems to have disappeared — it'll show up here with status 		"Exited" instead of vanishing entirely, which tells you it crashed rather than never started.
4	docker logs shopsphere-postgres
	 Shows the internal logs of that specific container — in Postgres's case, its own startup messages (e.g. "database system is ready to accept connections"). Use this to confirm Postgres itself started cleanly, or to see why it crashed if docker ps -a shows it exited.
5	docker exec -it shopsphere-postgres psql -U shopsphere_user -d shopsphere
	 Opens an interactive psql session directly inside the running Postgres container, logging in as shopsphere_user to the shopsphere database. It'll prompt for the password (shopsphere_pass). Use this to verify the database/user/password actually work, completely independent of your Spring Boot app — if this connects, Postgres is fine and the problem is on the Java/Eclipse side.
6	docker compose stop
	 Stops the running container(s) but keeps the data (the volume) intact. Use this when you're done working for the day — no need to keep Postgres running in the background all the time.
7	docker compose down
	 Stops AND removes the containers and network (but by default keeps volumes, so your data survives). Use this if a container gets into a weird state and you want a clean restart. Add -v at the end (docker compose down -v) only if you also want to wipe all data and start completely fresh — useful if your database schema gets into a broken state during learning/testing.
8	\dt / \du / \q (inside psql)
	 Once inside a psql session (after step 5), these are the basic commands to poke around: \dt lists tables, \du lists users/roles, \q exits back to your normal terminal. Use these to manually verify your Spring Boot app actually created the users/products/orders tables after a successful run.