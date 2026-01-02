# 🚀 SmartLogi CI/CD Pipeline Guide

## Complete Setup for Jenkins, JaCoCo & SonarQube

---

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Starting the Environment](#starting-the-environment)
3. [Configuring SonarQube](#configuring-sonarqube)
4. [Configuring Jenkins](#configuring-jenkins)
5. [Understanding the Jenkinsfile](#understanding-the-jenkinsfile)
6. [Running Your First Pipeline](#running-your-first-pipeline)
7. [Viewing Reports](#viewing-reports)
8. [Troubleshooting](#troubleshooting)

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        SmartLogi CI/CD                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────┐     ┌──────────┐     ┌──────────┐                │
│  │  GitHub  │────▶│  Jenkins │────▶│ SonarQube│                │
│  │   Repo   │     │  :8080   │     │  :9000   │                │
│  └──────────┘     └────┬─────┘     └──────────┘                │
│                        │                                        │
│                        ▼                                        │
│              ┌─────────────────┐                               │
│              │   Build & Test  │                               │
│              │   Maven + JDK17 │                               │
│              └────────┬────────┘                               │
│                       │                                         │
│         ┌─────────────┼─────────────┐                          │
│         ▼             ▼             ▼                          │
│   ┌──────────┐  ┌──────────┐  ┌──────────┐                    │
│   │  JaCoCo  │  │ JUnit    │  │ SonarQube│                    │
│   │ Coverage │  │ Reports  │  │ Analysis │                    │
│   └──────────┘  └──────────┘  └──────────┘                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Services & Ports

| Service        | Port | URL                   | Credentials             |
| -------------- | ---- | --------------------- | ----------------------- |
| **Jenkins**    | 8080 | http://localhost:8080 | See initial password    |
| **SonarQube**  | 9000 | http://localhost:9000 | admin / admin           |
| **PostgreSQL** | 5432 | localhost:5432        | user / password         |
| **pgAdmin**    | 9090 | http://localhost:9090 | admin@admin.com / admin |

---

## 🚀 Starting the Environment

### Step 1: Start Docker Containers

```powershell
# Navigate to project directory
cd C:\Users\Youcode\Desktop\smart-logi-1

# Start all services
docker-compose up -d

# Check containers status
docker-compose ps
```

### Step 2: Verify All Services Are Running

```powershell
# List running containers
docker ps

# Expected: db, pgadmin, sonarqube, sonarqube-db, jenkins all running
```

### Step 3: Wait for Services to Initialize

- **SonarQube**: Takes ~2-3 minutes to start (wait for "SonarQube is operational")
- **Jenkins**: Takes ~1-2 minutes to initialize

---

## 🔍 Configuring SonarQube

### Step 1: Access SonarQube

1. Open http://localhost:9000
2. Login with: **admin** / **admin**
3. You'll be prompted to change password → Change it

### Step 2: Generate Authentication Token

1. Click on your profile (top-right) → **My Account**
2. Go to **Security** tab
3. Generate Token:
   - Name: `jenkins-token`
   - Type: `Global Analysis Token`
   - Click **Generate**
4. **⚠️ COPY THE TOKEN** - You won't see it again!

### Step 3: Create Project

1. Click **Projects** → **Create Project** → **Manually**
2. Project Settings:
   - Project Key: `SmartLogi`
   - Display Name: `SmartLogi`
3. Click **Set up**
4. Choose **Locally** → **Generate Token** (or use existing)

---

## ⚙️ Configuring Jenkins

### Step 1: Get Initial Admin Password

```powershell
docker exec smart-logi-1-jenkins-1 cat /var/jenkins_home/secrets/initialAdminPassword
```

### Step 2: Initial Setup

1. Open http://localhost:8080
2. Paste the initial admin password
3. Click **Install suggested plugins**
4. Create admin user or skip (use admin)

### Step 3: Install Required Plugins

Go to: **Manage Jenkins** → **Plugins** → **Available plugins**

Install these plugins:

- ✅ **SonarQube Scanner**
- ✅ **JaCoCo**
- ✅ **HTML Publisher**
- ✅ **Pipeline**
- ✅ **Git**

Restart Jenkins after installation.

### Step 4: Configure JDK

1. Go to: **Manage Jenkins** → **Tools**
2. Scroll to **JDK installations**
3. Click **Add JDK**:
   - Name: `jdk-17`
   - ✅ Check "Install automatically"
   - Choose: **Install from adoptium.net** → **jdk-17.x.x**

### Step 5: Configure Maven

1. In same **Tools** page, scroll to **Maven installations**
2. Click **Add Maven**:
   - Name: `maven-3.8.9`
   - ✅ Check "Install automatically"
   - Version: **3.8.9**

### Step 6: Configure SonarQube Server

1. Go to: **Manage Jenkins** → **System**
2. Scroll to **SonarQube servers**
3. Click **Add SonarQube**:

   - Name: `SonarQube` (⚠️ must match Jenkinsfile)
   - Server URL: `http://sonarqube:9000` (internal Docker network)
   - Server authentication token:
     - Click **Add** → **Jenkins**
     - Kind: **Secret text**
     - Secret: _paste your SonarQube token_
     - ID: `sonarqube-token`
     - Click **Add**
   - Select the created credential

4. Click **Save**

---

## 📖 Understanding the Jenkinsfile

Your pipeline has 6 stages. Here's what each does:

### Stage 1: Checkout

```groovy
stage('Checkout') {
    steps {
        checkout scm
    }
}
```

**Purpose**: Clones your Git repository into Jenkins workspace

- `checkout scm` - Uses the SCM (Source Control Management) configured in the job
- Downloads all source code for the pipeline to work with

---

### Stage 2: Build & Test

```groovy
stage('Build & Test') {
    steps {
        sh 'mvn clean verify'
    }
}
```

**Purpose**: Compiles code and runs all unit tests

| Command      | Action                                 |
| ------------ | -------------------------------------- |
| `mvn clean`  | Deletes `/target` folder (fresh start) |
| `mvn verify` | Compiles → Tests → Verifies package    |

**What happens**:

1. Compiles `src/main/java` → `target/classes`
2. Compiles `src/test/java` → `target/test-classes`
3. Runs JUnit tests
4. Generates test reports in `target/surefire-reports/`
5. JaCoCo agent attaches to collect coverage data (`target/jacoco.exec`)

---

### Stage 3: Code Coverage (JaCoCo)

```groovy
stage('Code Coverage (JaCoCo)') {
    steps {
        sh 'mvn jacoco:report'
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Coverage'
            ])
        }
    }
}
```

**Purpose**: Generates code coverage reports

| Component           | Description                                    |
| ------------------- | ---------------------------------------------- |
| `mvn jacoco:report` | Converts `jacoco.exec` → HTML + XML reports    |
| `junit '.../*.xml'` | Publishes JUnit test results to Jenkins        |
| `publishHTML`       | Makes JaCoCo HTML report visible in Jenkins UI |

**Output Files**:

- `target/site/jacoco/index.html` - HTML coverage report
- `target/site/jacoco/jacoco.xml` - XML report (for SonarQube)
- `target/site/jacoco/jacoco.csv` - CSV export

---

### Stage 4: SonarQube Analysis

```groovy
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('SonarQube') {
            sh '''
                mvn sonar:sonar \
                -Dsonar.projectKey=SmartLogi \
                -Dsonar.projectName=SmartLogi \
                -Dsonar.java.coveragePlugin=jacoco \
                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                -Dsonar.junit.reportPaths=target/surefire-reports \
                -Dsonar.sources=src/main/java \
                -Dsonar.tests=src/test/java \
                -Dsonar.java.binaries=target/classes \
                -Dsonar.java.test.binaries=target/test-classes
            '''
        }
    }
}
```

**Purpose**: Sends code to SonarQube for quality analysis

| Parameter                              | Value                         | Description                 |
| -------------------------------------- | ----------------------------- | --------------------------- |
| `sonar.projectKey`                     | SmartLogi                     | Unique project identifier   |
| `sonar.projectName`                    | SmartLogi                     | Display name in SonarQube   |
| `sonar.java.coveragePlugin`            | jacoco                        | Coverage tool being used    |
| `sonar.coverage.jacoco.xmlReportPaths` | target/site/jacoco/jacoco.xml | Where to find coverage data |
| `sonar.junit.reportPaths`              | target/surefire-reports       | Test results location       |
| `sonar.sources`                        | src/main/java                 | Production code path        |
| `sonar.tests`                          | src/test/java                 | Test code path              |
| `sonar.java.binaries`                  | target/classes                | Compiled classes            |
| `sonar.java.test.binaries`             | target/test-classes           | Compiled test classes       |

**What SonarQube Analyzes**:

- 🐛 **Bugs** - Potential errors in code
- 🔓 **Vulnerabilities** - Security issues
- 💩 **Code Smells** - Maintainability issues
- 📊 **Coverage** - % of code tested
- 📝 **Duplications** - Copy-paste code

---

### Stage 5: Quality Gate

```groovy
stage('Quality Gate') {
    steps {
        timeout(time: 20, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
        }
    }
}
```

**Purpose**: Waits for SonarQube's verdict on code quality

| Setting         | Value  | Description                       |
| --------------- | ------ | --------------------------------- |
| `timeout`       | 20 min | Maximum wait time                 |
| `abortPipeline` | true   | Fails build if quality gate fails |

**Quality Gate Checks** (default):

- Coverage > 80%
- No new bugs
- No new vulnerabilities
- Duplication < 3%

**⚠️ Note**: For this to work, you need to configure a **Webhook** in SonarQube:

1. SonarQube → Administration → Configuration → Webhooks
2. Add webhook: `http://jenkins:8080/sonarqube-webhook/`

---

### Stage 6: Package

```groovy
stage('Package') {
    steps {
        sh 'mvn package -DskipTests'
        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
    }
}
```

**Purpose**: Creates the final JAR file

| Command                   | Description                          |
| ------------------------- | ------------------------------------ |
| `mvn package -DskipTests` | Builds JAR without re-running tests  |
| `archiveArtifacts`        | Stores JAR in Jenkins for download   |
| `fingerprint: true`       | Creates unique hash for traceability |

**Output**: `target/SmartLogi-0.0.1-SNAPSHOT.jar`

---

### Post Actions

```groovy
post {
    always {
        echo "Build ${currentBuild.result} - ${env.JOB_NAME} #${env.BUILD_NUMBER}"
    }
    success {
        echo 'Pipeline exécuté avec succès!'
    }
    failure {
        echo 'Pipeline a échoué!'
    }
}
```

**Purpose**: Actions that run after all stages complete

| Block     | When it runs                     |
| --------- | -------------------------------- |
| `always`  | Every time, regardless of result |
| `success` | Only when pipeline succeeds      |
| `failure` | Only when pipeline fails         |

---

## 🎯 Running Your First Pipeline

### Option A: Pipeline from SCM (Recommended)

1. **Create New Pipeline Job**

   - Jenkins Dashboard → **New Item**
   - Name: `SmartLogi-Pipeline`
   - Type: **Pipeline**
   - Click **OK**

2. **Configure Pipeline**

   - Scroll to **Pipeline** section
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: Your Git repo URL
   - Credentials: Add if private repo
   - Branch: `*/main` or `*/master`
   - Script Path: `Jenkinsfile`

3. **Save and Build**
   - Click **Save**
   - Click **Build Now**

### Option B: Direct Jenkinsfile (For Testing)

1. **Create New Pipeline Job**

   - Jenkins Dashboard → **New Item**
   - Name: `SmartLogi-Test`
   - Type: **Pipeline**
   - Click **OK**

2. **Paste Pipeline Script**

   - Scroll to **Pipeline** section
   - Definition: **Pipeline script**
   - Copy-paste content from `Jenkinsfile`

3. **Save and Build**

---

## 📊 Viewing Reports

### Jenkins Reports

| Report              | Location                  |
| ------------------- | ------------------------- |
| **Build History**   | Job → Left sidebar        |
| **Console Output**  | Build # → Console Output  |
| **JaCoCo Coverage** | Build # → JaCoCo Coverage |
| **Test Results**    | Build # → Test Result     |
| **Artifacts (JAR)** | Build # → Build Artifacts |

### SonarQube Dashboard

1. Go to http://localhost:9000
2. Click on **SmartLogi** project
3. View:
   - **Overview** - Summary of all metrics
   - **Issues** - List of bugs/vulnerabilities
   - **Measures** - Detailed metrics
   - **Code** - Browse source with issues highlighted

---

## 🔧 Troubleshooting

### Problem: SonarQube won't start

```powershell
# Check logs
docker logs smart-logi-1-sonarqube-1

# Common fix for Windows/WSL2
wsl -d docker-desktop sysctl -w vm.max_map_count=262144
```

### Problem: Jenkins can't find Maven/JDK

1. Go to **Manage Jenkins** → **Tools**
2. Verify tool names match Jenkinsfile exactly:
   - Maven: `maven-3.8.9`
   - JDK: `jdk-17`

### Problem: SonarQube connection refused

1. Verify SonarQube is running: http://localhost:9000
2. In Jenkins, use internal URL: `http://sonarqube:9000`
3. Check token is valid and not expired

### Problem: Quality Gate timeout

1. Add webhook in SonarQube:
   - URL: `http://jenkins:8080/sonarqube-webhook/`
2. Increase timeout in Jenkinsfile

### Problem: JaCoCo report empty

1. Ensure tests exist and run
2. Check `pom.xml` has JaCoCo plugin configured:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## 🛠️ Useful Commands

```powershell
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f jenkins
docker-compose logs -f sonarqube

# Restart a service
docker-compose restart jenkins

# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v

# Get Jenkins password
docker exec smart-logi-1-jenkins-1 cat /var/jenkins_home/secrets/initialAdminPassword
```

---

## ✅ Checklist

- [ ] Docker containers running (`docker-compose up -d`)
- [ ] SonarQube accessible at http://localhost:9000
- [ ] SonarQube token generated
- [ ] Jenkins accessible at http://localhost:8080
- [ ] Jenkins plugins installed (SonarQube Scanner, JaCoCo, HTML Publisher)
- [ ] JDK 17 configured in Jenkins
- [ ] Maven 3.8.9 configured in Jenkins
- [ ] SonarQube server configured in Jenkins
- [ ] Pipeline job created
- [ ] Webhook configured in SonarQube
- [ ] First build successful! 🎉

---

## 📚 Additional Resources

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [SonarQube Documentation](https://docs.sonarqube.org/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Maven Documentation](https://maven.apache.org/guides/)
