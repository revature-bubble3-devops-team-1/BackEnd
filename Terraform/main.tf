terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "4.11.0"
    }
     kubernetes = {
      version = "~> 2.10.0"
    }
    helm = {
      version = "~> 2.5.1"
    }
    random = {
      source  = "hashicorp/random"
      version = "3.1.0"
    }

    local = {
      source  = "hashicorp/local"
      version = "2.1.0"
    }

    null = {
      source  = "hashicorp/null"
      version = "3.1.0"
    }
  }
  required_version = ">= 0.14.9"
}

data "aws_availability_zones" "available" {}

provider "aws" {
  region = var.region
}

locals {
  cluster_name = "team-aqua-${random_string.suffix.result}"
}

resource "random_string" "suffix" {
  length  = 8
  special = false
}

resource "aws_default_vpc" "default" {
  tags = {
    Name = "Default VPC"
  }
}
  

provider "aws"{
  region = "us-east-1"
  access_key = ""
  secret_key = ""
}

resource "aws_instance" "aws_instance" {
  ami  = "ami-0c02fb55956c7d316"
  instance_type = "t2.micro"

    tags = {
    Name = "terraform"
  }

  resource "aws"
}



//EKS


module "eks" {
  source          = "terraform-aws-modules/eks/aws"
  version         = "18.20.5"
  cluster_name    = local.cluster_name
  cluster_version = "1.20"
  subnets         = module.vpc.private_subnets

  vpc_id = module.vpc.vpc_id

  workers_group_defaults = {
    root_volume_type = "gp2"
  }

  worker_groups = [
    {
      name                          = "worker-group-1"
      instance_type                 = "t2.medium"
      additional_security_group_ids = [aws_security_group.allow_tls.id]
      asg_desired_capacity          = 3
    }
  ]
}

data "aws_eks_cluster" "eks_cluster" {
  name = module.eks.cluster_id
}

data "aws_eks_cluster_auth" "auth_cluster" {
  name = module.eks.cluster_id
}


resource "aws_iam_role_policy_attachment" "" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.example.name
}


//RDS


//S3
resource "aws_s3_bucket" "s3" {
  bucket = "bubble-bucket"

  tags = {
    Name        = "bubble-bucket"
    Environment = "Prod"
  }
}

resource "aws_s3_bucket_acl" "bubble-bucket" {
  bucket = aws_s3_bucket.s3.id
  acl    = "private"
}


//Kubernetes

provider "kubernetes" {
  host                   = data.aws_eks_cluster.cluster.endpoint
  token                  = data.aws_eks_cluster_auth.cluster.token
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority.0.data)
}