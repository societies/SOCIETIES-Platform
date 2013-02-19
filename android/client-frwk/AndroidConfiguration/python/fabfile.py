from fabric.api import run, env

env.hosts = ['societiescloud@192.168.122.223']

def host_type():
    run('cat /home/societiescloud/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/config/xc.properties')
