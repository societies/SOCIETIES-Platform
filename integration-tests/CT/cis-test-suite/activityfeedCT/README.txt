This test requires two containers:
Container 1: full platform, including DA
Container 2: user bundles, connected to DA in Container 1.

Container 1 and 2 should have different and clean databases. No other CISes should be created on any of the containers.

(not sure: You need to configure owner CSS id on both containers.)
Procedure:
1. You launch container 1, wait for it to finish, so that the fullcontainer plan has status "started"/active.
2. You launch container 2, wait for it to finish.

3. Then you deploy "hostingbundle" in container 1. (typically by copy pasting the bundle .jar into the pickup directory of container 1)
4. Then you deploy "remotebundle" in container 2. (typically by copy pasting the bundle .jar into the pickup directory of container 2)

Both bundles should run out completely without failure
