namespace Folkeregister.Contracts.Types

open System

type SSN = {Value: string}
type Name = {Value: string}
type Address = {Street: string; StreetNumber: string; PostalCode: string; City: string}
