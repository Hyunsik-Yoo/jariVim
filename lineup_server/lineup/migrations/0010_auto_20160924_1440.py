# -*- coding: utf-8 -*-
# Generated by Django 1.9.8 on 2016-09-24 14:40
from __future__ import unicode_literals

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('lineup', '0009_auto_20160924_1439'),
    ]

    operations = [
        migrations.RenameModel(
            old_name='RestaurantList',
            new_name='restaurant_list',
        ),
        migrations.AlterField(
            model_name='vote',
            name='created_it',
            field=models.TimeField(default=datetime.datetime(2016, 9, 24, 14, 40, 45, 622233)),
        ),
    ]