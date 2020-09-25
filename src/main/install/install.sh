#!/bin/sh

cp movies-update /etc/cron.daily/ || exit 30
chmod +x /etc/cron.daily/movies-update
