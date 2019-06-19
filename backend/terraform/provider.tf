provider "aws" {
  region = "eu-north-1"
}

terraform {
  backend "s3" {
    bucket                 = "dev-fredag.cheerwithme-terraform"
    key                    = "terraform-state/state.tfstate"
    region                 = "eu-north-1"
    skip_region_validation = "true"
  }
}
