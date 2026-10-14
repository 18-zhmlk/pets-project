from rest_framework import viewsets, permissions, generics, status
from rest_framework.decorators import action, api_view, permission_classes
from rest_framework.response import Response
from django.contrib.auth import get_user_model
from .models import Pet
from .serializers import PetSerializer, UserSerializer, RegisterSerializer

User = get_user_model()

# Register endpoint (creates user and optional first pet)
class RegisterView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = RegisterSerializer
    permission_classes = [permissions.AllowAny]

# Profile (current user) - retrieve & update
class ProfileView(generics.RetrieveUpdateAPIView):
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_object(self):
        return self.request.user

# Pet CRUD
class PetViewSet(viewsets.ModelViewSet):
    serializer_class = PetSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    def get_queryset(self):
        # list pets of current user (for authenticated), otherwise empty
        user = self.request.user
        if user.is_authenticated:
            return Pet.objects.filter(owner=user).order_by('-created_at')
        return Pet.objects.none()

    def perform_create(self, serializer):
        serializer.save(owner=self.request.user)

    # Optional: endpoint to list all public pets if you want
    @action(detail=False, methods=['get'], permission_classes=[permissions.AllowAny])
    def public(self, request):
        qs = Pet.objects.all().order_by('-created_at')[:50]
        return Response(PetSerializer(qs, many=True).data)
