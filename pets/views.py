from rest_framework import viewsets, permissions
from .models import Pet
from .serializers import PetSerializer
from django.shortcuts import render

def home(request):
    return render(request, 'pets/home.html')

class PetViewSet(viewsets.ModelViewSet):
    serializer_class = PetSerializer

    def get_permissions(self):
        if self.action in ['list','retrieve']:
            return [permissions.AllowAny()]
        return [permissions.IsAuthenticated()]

    def get_queryset(self):
        user = self.request.user
        # Для list/retrieve вернём только питомцев текущего пользователя (если авторизован)
        if user.is_authenticated:
            return Pet.objects.filter(owner=user)
        # иначе — пустой набор
        return Pet.objects.none()

    def perform_create(self, serializer):
        serializer.save(owner=self.request.user)
