namespace Folkeregister.Contracts.Commands
open Folkeregister.Contracts.Types
open Folkeregister.Infrastructure
open System

type CreatePerson = {Id: Guid; SSN: SSN; Name: Name } with interface ICommand
type AddAdressToPerson = {Id: Guid; Address: Address} with interface ICommand

//type PlaceOrder = 
//    {UserId: Guid; ProductId: Guid; Quantity: int}
//    with interface ICommand
//
