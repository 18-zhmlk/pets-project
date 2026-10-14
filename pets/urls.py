from rest_framework.routers import DefaultRouter
from .views import PetViewSet
from django.urls import path, include
from . import views

router = DefaultRouter()
router.register('', PetViewSet, basename='pet')

urlpatterns = [
    path('', include(router.urls)),
    path('', views.home, name='home'),
]
