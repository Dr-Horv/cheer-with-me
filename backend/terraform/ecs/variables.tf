# main creds for AWS connection

variable "ecs_cluster" {
  description = "ECS cluster name"
  default     = "dev-fredag"
}

variable "ecs_key_pair_name" {
  description = "EC2 instance key pair name"
  default     = "dev-fredag"
}

variable "region" {
  description = "AWS region"
  default     = "eu-north-1"
}

variable "availability_zone" {
  description = "availability zone used for the demo, based on region"

  default = {
    eu-north-1 = "eu-north-1"
  }
}

########################### Test VPC Config ################################

variable "dev-fredag_vpc" {
  description = "VPC name for Test environment"
  default     = "dev-fredag"
}

variable "dev-fredag_network_cidr" {
  description = "IP addressing for Test Network"
  default     = "10.0.1.0/24"
}

variable "dev-fredag_public_01_cidr" {
  description = "Public 0.0 CIDR for externally accessible subnet"
  default     = "10.0.1.0/24"
}

variable "dev-fredag_public_02_cidr" {
  description = "Public 0.0 CIDR for externally accessible subnet"
  default     = "10.0.1.0/24"
}

########################### Autoscale Config ################################

variable "max_instance_size" {
  description = "Maximum number of instances in the cluster"
  default     = 1
}

variable "min_instance_size" {
  description = "Minimum number of instances in the cluster"
  default     = 1
}

variable "desired_capacity" {
  description = "Desired number of instances in the cluster"
  default     = 1
}
