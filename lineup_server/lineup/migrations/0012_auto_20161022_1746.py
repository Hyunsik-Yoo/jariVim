# -*- coding: utf-8 -*-
# Generated by Django 1.10.1 on 2016-10-22 08:46
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('lineup', '0011_auto_20160924_1444'),
    ]

    operations = [
        migrations.AlterField(
            model_name='vote',
            name='created_it',
            field=models.TimeField(auto_now_add=True),
        ),
    ]