from django.conf.urls import url
from lineup import views
from rest_framework.urlpatterns import format_suffix_patterns

urlpatterns = [
    url(r'^lineup/restaurants/$',views.restaurent_list),
    url(r'^lineup/vote/$',views.vote_list),
    url(r'^lineup/current/$',views.current),
]

urlpatterns = format_suffix_patterns(urlpatterns)