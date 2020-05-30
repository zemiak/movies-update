#!/bin/sh

cp movies-update /etc/cron.daily/ || exit 30
chmod +x /etc/cron.daily/movies-update

cp ../cron-moviethumbnails/movies-moviethumbnails /etc/cron.daily/ || exit 40
chmod +x /etc/cron.daily/movies-moviethumbnails
