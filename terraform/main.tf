provider "aws" {
  region = "eu-north-1"
}

resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
}

resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  availability_zone       = "eu-north-1b"
}

resource "aws_subnet" "private" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.2.0/24"
  availability_zone = "eu-north-1b"
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }
}

resource "aws_route_table_association" "public_assoc" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

# Security Group for App EC2
resource "aws_security_group" "app_sg" {
  name        = "app-sg"
  description = "Allow 22 and 8080"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Security Group for DB EC2
resource "aws_security_group" "db_sg" {
  name        = "db-sg"
  description = "Allow Postgres from App SG"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.app_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

variable "public_key" {
  description = "Public key content for EC2 access"
  type        = string
}

# Key Pair
resource "aws_key_pair" "deployer" {
  key_name   = "test_key_pair"
  public_key = var.public_key
}

# EC2 Instance - App
resource "aws_instance" "app" {
  ami           = "ami-0c1ac8a41498c1a9c"
  instance_type = "t3.micro"
  subnet_id     = aws_subnet.public.id
  associate_public_ip_address = true
  key_name      = aws_key_pair.deployer.key_name
  vpc_security_group_ids = [aws_security_group.app_sg.id]

  tags = {
    Name = "App-Server"
  }
}

# EC2 Instance - DB
resource "aws_instance" "db" {
  ami           = "ami-0c1ac8a41498c1a9c"
  instance_type = "t3.micro"
  subnet_id     = aws_subnet.private.id
  associate_public_ip_address = false
  key_name      = aws_key_pair.deployer.key_name
  vpc_security_group_ids = [aws_security_group.db_sg.id]

  tags = {
    Name = "DB-Server"
  }
}
