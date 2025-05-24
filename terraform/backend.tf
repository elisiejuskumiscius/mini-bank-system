terraform {
  backend "s3" {
    bucket         = "elisiejus-app-terraform-state-bucket"
    key            = "terraform/app/terraform.tfstate"
    region         = "eu-north-1"
    encrypt        = true
  }
}
