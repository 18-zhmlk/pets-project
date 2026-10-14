from rest_framework import serializers
from .models import User
from pets.serializers import PetSerializer

class UserSerializer(serializers.ModelSerializer):
    pets = PetSerializer(many=True, read_only=True)

    class Meta:
        model = User
        fields = ['id','username','email','phone','avatar','pets']
        read_only_fields = ['id','pets']

class RegisterSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)
    pet_name = serializers.CharField(write_only=True, required=False)
    pet_type = serializers.ChoiceField(choices=[('cat','Cat'),('dog','Dog'),('other','Other')],
                                       write_only=True, required=False)
    pet_photo = serializers.ImageField(write_only=True, required=False)

    class Meta:
        model = User
        fields = ['username','email','phone','password','avatar','pet_name','pet_type','pet_photo']

    def create(self, validated_data):
        pet_name = validated_data.pop('pet_name', None)
        pet_type = validated_data.pop('pet_type', None)
        pet_photo = validated_data.pop('pet_photo', None)
        password = validated_data.pop('password')
        user = User(**validated_data)
        user.set_password(password)
        user.save()

        # Создаём питомца, если переданы поля pet_name
        if pet_name:
            from pets.models import Pet
            Pet.objects.create(owner=user, name=pet_name, pet_type=pet_type or 'other', photo=pet_photo)
        return user
