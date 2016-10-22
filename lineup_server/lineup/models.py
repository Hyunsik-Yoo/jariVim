from __future__ import unicode_literals

from django.db import models
import datetime

class Vote(models.Model):
    created_it = models.TimeField(blank=False, auto_now_add=True)
    title = models.CharField(max_length=100, blank=False, default='')
    proportion = models.IntegerField(blank=False, default=0)


class RestaurantList(models.Model):
    category = models.CharField(max_length=100, blank=False, default='bob')
    title = models.CharField(max_length=100, blank=False, default='')


# Create your models here.
