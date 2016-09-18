# -*- coding: utf-8 -*-
from rest_framework import serializers
from lineup.models import Vote, restaurant_list


class restaurantSerializer(serializers.Serializer):
    category = serializers.CharField(required=True, allow_blank=False, max_length=100)
    title = serializers.CharField(required=True, allow_blank=False, max_length=100)

    def create(self, validated_data):
        return restaurant_list.objects.create(**validated_data)

    def update(self, instance, validated_data):

        instance.cagegory = validated_data.get('category', instance.cagegory)
        instance.title = validated_data.get('title',instance.title)
        instance.save()
        return instance

class voteSerializer(serializers.Serializer):
    title = serializers.CharField(required=True, allow_blank=False, max_length=100)
    proportion = serializers.IntegerField(default=0)

    def create(self, validated_data):
        return Vote.objects.create(**validated_data)

    def update(self, instance, validated_data):
        instance.title = validated_data.get('title',instance.title)
        instance.proportion = validated_data.get('proportion',instance.proportion)
        instance.save()
        return instance

