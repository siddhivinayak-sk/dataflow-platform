https://stackoverflow.com/questions/66338549/wsl2-network-unreachable
https://docs.microsoft.com/en-us/windows/wsl/install-manual
https://towardsdatascience.com/run-apache-airflow-on-windows-10-without-docker-3c5754bb98b4
https://airflow.apache.org/docs/apache-airflow/stable/start/local.html

Obuntu on Windows:
Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Windows-Subsystem-Linux
Enable-WindowsOptionalFeature -Online -FeatureName VirtualMachinePlatform
wsl --set-default-version 2
Invoke-WebRequest -Uri https://wsldownload.azureedge.net/Ubuntu_1604.2019.523.0_x64.appx -OutFile Ubuntu.appx -UseBasicParsing
Add-AppxPackage .\Ubuntu.appx
Add user: sk/pwd

Get-AppxPackage *ubuntu*
Get-AppxPackage CanonicalGroupLimited.Ubuntu16.04onWindows | Remove-AppxPackage
Disable-WindowsOptionalFeature -Online -FeatureName Microsoft-Windows-Subsystem-Linux

Installation:
sudo apt update && sudo apt upgrade
sudo apt-get install software-properties-common
sudo apt-add-repository universe
sudo apt-get update
sudo apt install python-pip --fix-missing
sudo apt-get install python3-pip
pip3 install apache-airflow
pip install --upgrade pip


export AIRFLOW_HOME=~/airflow
AIRFLOW_VERSION=2.2.2
PYTHON_VERSION="$(python --version | cut -d " " -f 2 | cut -d "." -f 1-2)"
CONSTRAINT_URL="https://raw.githubusercontent.com/apache/airflow/constraints-${AIRFLOW_VERSION}/constraints-${PYTHON_VERSION}.txt"
pip install "apache-airflow==${AIRFLOW_VERSION}" --constraint "${CONSTRAINT_URL}"
pip install "apache-airflow==${AIRFLOW_VERSION}" --constraint "constraints-3.6.txt"
pip install virtualenv
airflow standalone
http://localhost:8080/
