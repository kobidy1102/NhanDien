using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using Microsoft.ProjectOxford.Face;
using Microsoft.ProjectOxford.Face.Contract;
using System.IO;
using System.Net.Http.Headers;
using System.Drawing;

namespace router.Controllers
{
    public class ValuesController : ApiController
    {
        string kq = "Nguyễn Thanh Huy";
        FaceServiceClient faceServiceClient = new FaceServiceClient("5243a5f84e4645579af545b9cba4c496", "https://westcentralus.api.cognitive.microsoft.com/face/v1.0");

        // GET api/values
        //public IEnumerable<string> Get()
        //{
        //    return new string[] { "value1", "value2" };
        //}

        //public async Task<string> Get()
        //{
        //    //var result = await RecognitionFaceImgPath("kpop", @"D:\test\gd.jpg");
        //    // string i = Test();
        //    return "";
        //}


        //// GET api/values/5
        //public string Get(int id)
        //{
        //    return "value";
        //}

        //[Route("customers={customerId}")]
        //public string Get(string customerId)
        //{
        //    return customerId;
        //}
        //[HttpPost]
        //[Route("data={data}")]
        //public string postdata(string data)
        //{
        //    return data;
        //}

        // POST api/values
        //public void Post([FromBody]string value)
        //{
        //}

        //// PUT api/values/5
        //public void Put(int id, [FromBody]string value)
        //{
        //}

        //// DELETE api/values/5
        //public void Delete(int id)
        //{
        //}



        [HttpPost, Route("api/recognition/{personGroup}")]
        public async Task<String> recognition(string personGroup)

        {
            string result = "Có lỗi khi upload ảnh ";
            var httpRequest = HttpContext.Current.Request;

            if (httpRequest.Files.Count == 1)
            {
                var postedFile = httpRequest.Files[0];
                Stream file_stream = postedFile.InputStream;
                result = await RecognitionFaceImgPath(personGroup, file_stream);
            }

            return result;

        }


        [HttpPost, Route("api/addPersonToGroup/{personGroup}/{name}")]
        public async Task<String> addPersonToGroup(string personGroup, string name)

        {
            string result = ".";
            result = await AddPersonToGroup(personGroup, name);
            return result;

        }



        [HttpPost, Route("api/addFaceToPerson/{personGroup}/{name}")]
        public async Task<String> addFaceToPerson(string personGroup, string name)

        {
            String result = "Có lỗi khi upload ảnh ";
            var httpRequest = HttpContext.Current.Request;

            if (httpRequest.Files.Count == 1)
            {
                var postedFile = httpRequest.Files[0];
                Stream file_stream = postedFile.InputStream;
                result = await AddFaceToPerson(personGroup, name, file_stream);
            }

            return result;

        }




        [HttpPost, Route("api/trainingAI/{personGroup}")]
        public async Task<String> trainingAI(string personGroup)

        {
            string result = "Training error ";
            result = await TrainingAI(personGroup);

            return result;

        }


        [HttpPost, Route("api/deletePerson/{personGroup}/{personId}")]
        public async Task<String> deletePersonRouter(string personGroup, string personId)

        {
            string result = "Training error ";
            result = await deletePerson(personGroup,personId);

            return result;

        }


        [HttpPost, Route("api/createPersonGroup/{personGroup}/{personGroupName}")]
        public async Task<String> createPersonGroup2(string personGroup, string personGroupName)

        {
            string result = "w";
            result = await CreatePersonGroup(personGroup, personGroupName);

            return result;

        }





        /////////
        ////////
        ////////


        private async Task<string> CreatePersonGroup(string personGroupId, string personGroupName)
        {
            try
            {
                await faceServiceClient.CreatePersonGroupAsync(personGroupId, personGroupName);
                return "complete";

            }
            catch (Exception ex)
            {
                return "error";
            }
        }



        public async Task<string> AddPersonToGroup(string personGroupId, string Name)
        {
            try
            {
                await faceServiceClient.GetPersonGroupAsync(personGroupId);
                CreatePersonResult person = await faceServiceClient.CreatePersonAsync(personGroupId, Name);
                //  await faceServiceClient.AddPersonFaceAsync(personGroupId, person.PersonId, s);
                return ""+person.PersonId;
            }
            catch (Exception ex)
            {
                return "Add person error";
            }


        }

        private async Task<string> AddFaceToPerson(string personGroupId, string strPersonId, Stream s)
        {
            try
            {
                Guid personId = new Guid(strPersonId);
                await faceServiceClient.AddPersonFaceAsync(personGroupId, personId, s);
                return "Add image";
            }
            catch (Exception ex)
            {
                return "Add image error";
            }
        }


        public async Task<string> TrainingAI(string personGroupId)
        {
            await faceServiceClient.TrainPersonGroupAsync(personGroupId);
            TrainingStatus trainingStatus = null;
            while (true)
            {
                trainingStatus = await faceServiceClient.GetPersonGroupTrainingStatusAsync(personGroupId);
                if (trainingStatus.Status != Status.Running)
                    break;
                await Task.Delay(1000);

            }
            return "Training complete";

        }



        public async Task<string> RecognitionFaceImgPath(string personGroupId, Stream stream)
        {

            string kq="";
            var faces = await faceServiceClient.DetectAsync(stream);
            var faceIds = faces.Select(face => face.FaceId).ToArray();
            try
            {
                var results = await faceServiceClient.IdentifyAsync(personGroupId, faceIds);
                foreach (var identifyResult in results)
                {
                    if (identifyResult.Candidates.Length == 0)
                    {
                        kq = kq+"Không nhận diện được \n";
                    }
                    else
                    {
                        var candidateId = identifyResult.Candidates[0].PersonId;
                        double rate = identifyResult.Candidates[0].Confidence;
                        rate = rate * 100;

                        var person = await faceServiceClient.GetPersonAsync(personGroupId, candidateId);
                        kq = kq+ string.Format("{0}", person.Name + " - " + rate+"% \n");
                    }
                }
            }
            catch (Exception ex)
            {
                // Console.WriteLine("Error Recognition Face" + ex.Message);
                kq = kq+" Không có khuôn mặt nào được phát hiện \n";
            }

            return kq;
        }

        public async Task<string> deletePersonGroup(string personGroupId)
        {
            try
            {

                await faceServiceClient.DeletePersonGroupAsync(personGroupId);
                return "Deleted personGroup ";
            }
            catch (Exception ex)
            {
                return "Delete error";
            }
        }




        public async Task<string> deletePerson(string personGroupId, string personId)
        {
            try
            {
                Guid g = new Guid(personId);
                await faceServiceClient.DeletePersonFromPersonGroupAsync(personGroupId, g);
                return "Deleted person";
            }
            catch (Exception ex)
            {
                return "Delete error";
            }








        }
    }




    namespace UploadFile.Custom
    {
        public class CustomUploadMultipartFormProvider : MultipartFormDataStreamProvider
        {
            public CustomUploadMultipartFormProvider(string path) : base(path) { }

            public override string GetLocalFileName(HttpContentHeaders headers)
            {
                if (headers != null && headers.ContentDisposition != null)
                {
                    return headers
                        .ContentDisposition
                        .FileName.TrimEnd('"').TrimStart('"');
                }

                return base.GetLocalFileName(headers);
            }


        }
    }
}
