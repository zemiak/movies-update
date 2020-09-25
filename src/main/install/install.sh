#!/bin/sh

mkdir -p /usr/local/bin
curl -L https://github.com/zemiak/movies-update/releases/download/latest/movies-update-runner.jar >/usr/local/bin/movies-update-runner.jar || exit 10
cp movies-update /etc/cron.daily/ || exit 30
chmod +x /etc/cron.daily/movies-update
