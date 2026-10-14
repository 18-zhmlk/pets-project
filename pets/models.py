# pets/models.py
from django.db import models
from accounts.models import Owner # <<< ОБЯЗАТЕЛЬНЫЙ ИМПОРТ ИЗ ПРИЛОЖЕНИЯ accounts

# ==============================================================================
# 1. PET (Питомец)
# ==============================================================================

class Pet(models.Model):
    """Основная информация о питомце."""
    
    PET_TYPES = (
        ('Dog', 'Собака'),
        ('Cat', 'Кот'),
        ('Other', 'Другое'),
    )
    SEX_CHOICES = (
        ('M', 'Мужской'),
        ('F', 'Женский'),
    )

    # Связь с владельцем
    owner = models.ForeignKey(
        Owner, 
        on_delete=models.CASCADE, 
        related_name='pets', 
        verbose_name="Владелец"
    )
    
    # Основные данные
    name = models.CharField(max_length=50, verbose_name="Имя питомца")
    type = models.CharField(max_length=10, choices=PET_TYPES, verbose_name="Тип")
    breed = models.CharField(max_length=100, blank=True, null=True, verbose_name="Порода")
    sex = models.CharField(max_length=1, choices=SEX_CHOICES, verbose_name="Пол")
    birth_date = models.DateField(verbose_name="Дата рождения")
    weight = models.FloatField(blank=True, null=True, verbose_name="Вес (кг)")
    
    # Дополнительные поля для профиля/здоровья/главного экрана
    avatar_image = models.ImageField(
        upload_to='pet_avatars/', 
        blank=True, 
        null=True, 
        verbose_name="Фото"
    )
    petnote_link_date = models.DateField(
        blank=True, 
        null=True, 
        verbose_name="Дата подключения к PetNote"
    )
    total_operations_count = models.IntegerField(
        default=0, 
        verbose_name="Количество выполненных операций"
    )
    
    # Поле для Главного экрана
    pet_quote = models.CharField(
        max_length=255, 
        default="Моя миска пуста. Это катастрофа.", 
        verbose_name="Мысль дня"
    )

    class Meta:
        verbose_name = "Питомец"
        verbose_name_plural = "Питомцы"

    def __str__(self):
        return f"{self.name} ({self.owner.full_name})"

# ==============================================================================
# 2. PLANNERTASK (Задачи планировщика)
# ==============================================================================

class PlannerTask(models.Model):
    """Задачи и события в планировщике (Планы)."""
    
    CATEGORY_CHOICES = (
        ('Daily', 'Ежедневный план'),
        ('Special', 'Особые события'),
    )

    pet = models.ForeignKey(
        Pet, 
        on_delete=models.CASCADE, 
        related_name='tasks', 
        verbose_name="Питомец"
    )
    
    date = models.DateField(verbose_name="Дата")
    description = models.CharField(max_length=255, verbose_name="Описание задачи/события")
    category = models.CharField(max_length=10, choices=CATEGORY_CHOICES, default='Daily', verbose_name="Категория")
    is_completed = models.BooleanField(default=False, verbose_name="Выполнено")

    class Meta:
        verbose_name = "Задача планировщика"
        verbose_name_plural = "Задачи планировщика"
        ordering = ['date'] 

# ==============================================================================
# 3. MEDICALRECORD (Медицинские записи)
# ==============================================================================

class MedicalRecord(models.Model):
    """Записи о вакцинации, визитах к ветеринару и осмотрах (Здоровье)."""
    
    RECORD_TYPES = (
        ('Vaccine', 'Вакцинация'),
        ('Visit', 'Визит к врачу'),
        ('Checkup', 'Плановый осмотр'),
    )

    pet = models.ForeignKey(
        Pet, 
        on_delete=models.CASCADE, 
        related_name='medical_records', 
        verbose_name="Питомец"
    )
    
    record_type = models.CharField(max_length=10, choices=RECORD_TYPES, default='Vaccine', verbose_name="Тип записи")
    description = models.CharField(max_length=255, verbose_name="Название/подробности")
    date = models.DateField(verbose_name="Дата проведения")
    
    # Контакт ветеринара (для поля "Ветеринар" на экране Здоровье)
    vet_contact = models.CharField(
        max_length=50, 
        blank=True, 
        null=True, 
        verbose_name="Контакт ветеринара"
    )

    class Meta:
        verbose_name = "Медицинская запись"
        verbose_name_plural = "Медицинские записи"
        ordering = ['-date'] 

# ==============================================================================
# 4. ALLERGY (Аллергии)
# ==============================================================================

class Allergy(models.Model):
    """Записи об аллергиях питомца (Здоровье)."""
    
    pet = models.ForeignKey(
        Pet, 
        on_delete=models.CASCADE, 
        related_name='allergies', 
        verbose_name="Питомец"
    )
    
    allergen = models.CharField(max_length=100, verbose_name="Аллерген")
    
    class Meta:
        verbose_name = "Аллергия"
        verbose_name_plural = "Аллергии"