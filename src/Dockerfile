# Dockerfile
# 1️⃣ نختارو صورة Java base
FROM eclipse-temurin:17-jdk-alpine

# 2️⃣ نحدد مكان العمل داخل الحاوية
WORKDIR /app

# 3️⃣ ننسخو الـ jar أو ملفات المشروع
COPY target/SmartLogi-0.0.1-SNAPSHOT.jar app.jar

# 4️⃣ نحدد الأمر اللي غادي يشغل التطبيق
ENTRYPOINT ["java", "-jar", "app.jar"]

# 5️⃣ اختيارياً نفتح port
EXPOSE 8080
