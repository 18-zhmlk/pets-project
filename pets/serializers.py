from rest_framework import serializers
from .models import Pet

class PetSerializer(serializers.ModelSerializer):
    class Meta:
        model = Pet
        fields = ['id','owner','name','pet_type','breed','gender','birth_date','photo','notes','created_at']
        read_only_fields = ['owner','created_at']
