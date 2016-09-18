from django.shortcuts import render
from django.http import HttpResponse

from rest_framework.renderers import JSONRenderer
from rest_framework.parsers import JSONParser
from lineup.models import Vote, restaurant_list
from lineup.serializers import restaurantSerializer, voteSerializer
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from datetime import datetime, timedelta



class JSONResponse(HttpResponse):
    """
    An HttpResponse that renders its content into JSON.
    """

    def __init__(self, data, **kwargs):
        content = JSONRenderer().render(data)
        kwargs['content_type'] = 'application/json'
        super(JSONResponse, self).__init__(content, **kwargs)

@api_view(['GET'])
def restaurent_list(request):
    try:
        parm_category = request.GET['category']
    except:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == 'GET':
        list = restaurant_list.objects.filter(category=parm_category)
        serializer = restaurantSerializer(list, many=True)
        return JSONResponse(serializer.data)


@api_view(['GET','POST'])
def vote_list(request):
    try:
        parm_title = request.GET['title']
        print parm_title
    except:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == 'GET':

        time_threshold = datetime.now() - timedelta(minutes=5)
        print time_threshold
        list = Vote.objects.filter(created_it__gt=time_threshold, title = parm_title)# __gt means "greater than" and lookup type field.

        serializer = voteSerializer(list, many=True)
        return JSONResponse(serializer.data)

    elif request.method == 'POST':
        serializer = voteSerializer(title = request.GET['title'], proportion = request.GET['proportion'])
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

