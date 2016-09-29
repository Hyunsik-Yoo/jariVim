# -*- coding: utf-8 -*-

from django.shortcuts import render
from django.http import HttpResponse

from rest_framework.renderers import JSONRenderer
from rest_framework.parsers import JSONParser
from lineup.models import Vote, RestaurantList
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
        list = RestaurantList.objects.filter(category=parm_category)
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


#카테고리별 가게이름을 먼저 조회. (vote가 안된 가게들도 있기 때문에 이런 방법을 채택)
#조회된 가게이름별로 최근 5분간의 인구비율을 계산하여 제공
@api_view(['GET'])
def current(request):
    try:
        parm_category = request.GET['category']
        print(parm_category)
    except:
        return Response(status=status.HTTP_404_NOT_FOUND)

    list_title = list(RestaurantList.objects.filter(category=parm_category))
    response = []
    for item in list_title:
        print item.title
        time_threshold = datetime.now() - timedelta(minutes=5)
        print time_threshold
        filter_vote = Vote.objects.filter(created_it__gt=time_threshold
                                   ,title=item.title)  # __gt means "greater than" and lookup type field.
        list_vote = []

        for vote in filter_vote:
            list_vote.append(vote.proportion)
        try:
            proportion = sum(list_vote)/len(list_vote)
        except:
            proportion = 0
        print(proportion)

        response.append({'title':item.title,'proportion':proportion})

    result = {"data":response}
    print(result)
    return Response(result, status=status.HTTP_201_CREATED)

@api_view(['GET'])
def vote(request):
    try:
        parm_title = request.GET['title']
        parm_proportion = request.GET['proportion']
        print "parm_title : ",parm_title, "parm_proportion : ", parm_proportion
        vote = Vote(title=parm_title, proportion=int(parm_proportion))
        vote.save()
        return Response("Success",status=status.HTTP_201_CREATED)
    except:
        return Response(status=status.HTTP_404_NOT_FOUND)




