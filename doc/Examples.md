== User Management

Install:

version=1.0.0-SNAPSHOT
install -s mvn:de.mhus.app.reactive/cr-example-user-management/$version
pdeploy -a de.mhus.cr.examples.users.AccountProcess:0.0.1


Register user:

pengine execute bpm://de.mhus.cr.examples.users.AccountProcess:0.0.1/de.mhus.cr.examples.users.register.RegisterUser

pengine execute bpme://node id


Forgot password:

pengine execute bpm://de.mhus.cr.examples.users.AccountProcess:0.0.1/de.mhus.cr.examples.users.password.ForgotPassword

pengine execute 'bpme://node id?password=newPassword'

